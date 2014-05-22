Whodunit
=======

Simple mod for tracking calls to selected methods.

Configuration
-------------
Mod is dormant unless it founds config file `whodunit.json` in Minecraft's `config` folder.

Example file contents (also included in [examples folder](examples)):

```javascript
{
    "methods" : {
        "chunk_load" : {
            "cls" : "net.minecraft.world.chunk.storage.AnvilChunkLoader",
            "argTypes" : ["Lnet/minecraft/world/World;", "I", "I"],
            "returnType" : "Lnet/minecraft/world/chunk/Chunk;",
            "mcpName" : "loadChunk",
            "srgName" : "func_75815_a"
        }
    }
}
```

Note: this file works for both MC 1.6.4 and 1.7.2.

Viewing results
---------------
To store collected information use `call_tracking dump` command.
Usage: `call_tracking dump <location> <format>` where `location` is name of method (in future, also group of methods) and `format` is format of resulting file (like `graphviz` or `pretty-json`)
