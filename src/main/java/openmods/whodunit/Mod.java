package openmods.whodunit;

import java.util.Arrays;

import openmods.whodunit.data.CommandDump;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class Mod extends DummyModContainer {

    public Mod() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "whodunit";
        meta.name = "Whodunit?";
        meta.version = "@VERSION@";
        meta.authorList = Arrays.asList("boq");
        meta.url = "http://openmods.info/";
        meta.description = "So we know who is to blame...";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void onModConstruct(FMLConstructionEvent evt) {}

    @Subscribe
    public void onServerStart(FMLServerStartingEvent evt) {
        evt.registerServerCommand(new CommandDump(Setup.locations));
    }
}
