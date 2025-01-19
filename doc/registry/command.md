# KesseractJS - Command Registry

use `event.getCommands()` or `event.commands` to get the `Commands` object for building you command tree

use `event.getArguments()` or `event.arguments` for building commands with arguments, where you can get builtin arg types.

After getting the arg type, use `.get(event)` for creating arg type used by command tree, use `.getResult(context, result)` for parsing command arg

simple command:
```js
onEvent('command.registry', event => {
    const Commands = event.getCommands()
    const Arguments = event.getArguments()
    
    event.register(Commands.literal("say_wow")
        .excutes(context => {
            const player = context.source.playerOrException.asKJS()
            player.tell("wow")
        }))
})
```

command with arg:
```js
onEvent('command.registry', event => {
    const Commands = event.getCommands()
    const Arguments = event.getArguments()
    
    event.register(
        Commands.literal("arg_test")
            .then(Commands.argument('bl', Arguments.BOOLEAN.create(event)))
            .executes(context => {
                const got = Arguments.BOOLEAN.getResult(context, 'bl')
                const player = context.source.playerOrException.asKJS()
                player.tell(got)
                return 1;
            })
    )
})
```