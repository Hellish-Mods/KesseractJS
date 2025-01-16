package dev.latvian.kubejs.script.data;

import dev.latvian.kubejs.DevProperties;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.util.ConsoleJS;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author LatvianModder
 */
public abstract class KubeJSResourcePack implements ExportablePackResources {
    private final PackType packType;
    private Map<ResourceLocation, GeneratedData> generated;
    private Set<String> generatedNamespaces;

    public KubeJSResourcePack(PackType type) {
        packType = type;
    }

    private static String getFullPath(PackType type, ResourceLocation location) {
        return String.format("%s/%s/%s", type.getDirectory(), location.getNamespace(), location.getPath());
    }

    @Override
    public InputStream getRootResource(String fileName) throws IOException {
        return switch (fileName) {
            case PACK_META -> GeneratedData.PACK_META.get();
            case "pack.png" -> GeneratedData.PACK_ICON.get();
            default -> throw new ResourcePackFileNotFoundException(KubeJSPaths.DIRECTORY.toFile(), fileName);
        };
    }

    @Override
    public InputStream getResource(PackType type, ResourceLocation location) throws IOException {
        val generated = type == packType ? getGenerated().get(location) : null;

        if (generated == GeneratedData.INTERNAL_RELOAD) {
            close();
        }

        if (generated != null) {
            return generated.get();
        }

        throw new ResourcePackFileNotFoundException(KubeJSPaths.DIRECTORY.toFile(), getFullPath(type, location));
    }

    @Override
    public boolean hasResource(PackType type, ResourceLocation location) {
        return type == packType && getGenerated().get(location) != null;
    }

    /**
     * {@link GeneratedData#id()} -> {@link GeneratedData}
     */
    public Map<ResourceLocation, GeneratedData> getGenerated() {
        if (generated == null) {
            generated = new HashMap<>();
            generate(generated);

            val debug = DevProperties.get().logGeneratedData || DevProperties.get().debugInfo;

            try {
                val root = KubeJSPaths.get(packType);

                for (val dir : Files.list(root).filter(Files::isDirectory).toList()) {
                    val name = dir.getFileName().toString();

                    if (debug) {
                        KubeJS.LOGGER.info("# Walking namespace '{}'", name);
                    }

                    for (val path : Files.walk(dir)
                        .filter(KubeJSResourcePack::filterPath)
                        .toList()
                    ) {

                        val data = GeneratedData.of(
                            name,
                            dir.relativize(path).toString().replace('\\', '/').toLowerCase(),
                            () -> {
                                try {
                                    return Files.readAllBytes(path);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    return new byte[0];
                                }
                            }
                        );

                        if (debug) {
                            KubeJS.LOGGER.info(
                                "- File found: '{}' ({} bytes)",
                                data.id(),
                                data.data().get().length
                            );
                        }

                        if (skipFile(data)) {
                            if (debug) {
                                KubeJS.LOGGER.info("- Skipping '{}'", data.id());
                            }
                            continue;
                        }

                        generated.put(data.id(), data);
                    }
                }
            } catch (Exception ex) {
                KubeJS.LOGGER.error(
                    "Failed to load files from kubejs/{}",
                    packType.getDirectory(),
                    ex
                );
            }

            generated.put(GeneratedData.INTERNAL_RELOAD.id(), GeneratedData.INTERNAL_RELOAD);

            generated = Map.copyOf(generated);

            if (debug) {
                KubeJS.LOGGER.info("Generated {} data ({} files)", packType, generated.size());
            }
        }

        return generated;
    }

    public void generate(Map<ResourceLocation, GeneratedData> map) {
    }

    protected boolean skipFile(GeneratedData data) {
        return false;
    }

    @Override
    public Collection<ResourceLocation> getResources(
        PackType type,
        String namespace,
        String path,
        int maxDepth,
        Predicate<String> filter
    ) {
        if (type != packType) {
            return Collections.emptySet();
        }
        if (!path.endsWith("/")) {
            path = path + "/";
        }

        val filtered = new ArrayList<ResourceLocation>();

        for (val generated : getGenerated().values()) {
            val id = generated.id();

            if (id.getNamespace().equals(namespace)
                && id.getPath().startsWith(path)
                && filter.test(id.getPath())
            ) {
                filtered.add(id);
            }
        }

        return filtered;
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        if (type != packType) {
            return Collections.emptySet();
        }
        if (generatedNamespaces == null) {
            generatedNamespaces = getGenerated()
                .keySet()
                .stream()
                .map(ResourceLocation::getNamespace)
                .collect(Collectors.toSet());
        }

        return generatedNamespaces;
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) throws IOException {
        try (val in = this.getRootResource(PACK_META)) {
            return AbstractPackResources.getMetadataFromStream(serializer, in);
        }
    }

    @Override
    public String getName() {
        return "KubeJS Resource Pack [" + packType.getDirectory() + "]";
    }

    @Override
    public void close() {
        generated = null;
        generatedNamespaces = null;
    }

    @Override
    public void export(Path root) throws IOException {
        for (val file : getGenerated().entrySet()) {
            val path = root.resolve(
                packType.getDirectory() + "/" + file.getKey().getNamespace() + "/" + file.getKey().getPath());
            Files.createDirectories(path.getParent());
            Files.write(path, file.getValue().data().get());
        }

        Files.write(root.resolve(PACK_META), GeneratedData.PACK_META.data().get());
        Files.write(root.resolve("pack.png"), GeneratedData.PACK_ICON.data().get());
    }

    public static Stream<Path> tryWalk(Path path) {
        try {
            return Files.walk(path);
        } catch (Exception ignore) {
        }

        return Stream.empty();
    }

    public static void scanForInvalidFiles(String pathName, Path path) throws IOException {
        Files.list(path)
            .filter(Files::isDirectory)
            .flatMap(KubeJSResourcePack::tryWalk)
            .filter(KubeJSResourcePack::filterPath)
            .forEach(p -> {
                try {
                    val fileName = p.getFileName().toString().toCharArray();

                    for (val c : fileName) {
                        if (c >= 'A' && c <= 'Z') {
                            val pathForLog = path.relativize(p)
                                .toString()
                                .replace('\\', '/');
                            ConsoleJS.STARTUP.errorf("Invalid file name: Uppercase '%s' in %s%s",
                                c,
                                pathName,
                                pathForLog
                            );
                            break;
                        } else if (!ResourceLocation.validPathChar(c)) {
                            val pathForLog = path.relativize(p)
                                .toString()
                                .replace('\\', '/');
                            ConsoleJS.STARTUP.errorf("Invalid file name: Invalid character '%s' in %s%s",
                                c,
                                pathName,
                                pathForLog
                            );
                            break;
                        }
                    }
                } catch (Exception ex) {
                    val pathForLog = path.relativize(p)
                        .toString()
                        .replace('\\', '/');
                    ConsoleJS.STARTUP.error("Invalid file name: %s%s".formatted(pathName, pathForLog));
                }
            });
    }

    private static boolean filterPath(Path path) {
        try {
            if (!Files.isReadable(path)
                || !Files.isRegularFile(path)
                || Files.isHidden(path)) {
                return false;
            }
            val name = path.getFileName().toString().toLowerCase(Locale.ROOT);
            return !name.endsWith(".zip")
                && !name.equals(".ds_store")
                && !name.equals("thumbs.db")
                && !name.equals("desktop.ini");
        } catch (IOException e) {
            KubeJS.LOGGER.error(
                "unable to determine whether file with path {} is valid, skipping",
                path
            );
        }
        return false;
    }
}
