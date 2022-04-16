package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import org.graalvm.polyglot.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CommonJSPlugin implements Plugin {

    public static Engine jsEngine = null;

    protected String pluginName;
    protected File pluginDir;
    protected File mainJSFile;
    protected Server server;

    private boolean isEnabled = false;
    private boolean initialized = false;

    private PluginDescription description;
    private JSPluginLoader jsPluginLoader;
    private PluginLogger logger;

    protected Context jsContext = null;
    protected Value jsGlobal = null;

    public final void init(JSPluginLoader jsPluginLoader, File pluginDir, PluginDescription pluginDescription) {
        this.jsPluginLoader = jsPluginLoader;
        this.server = jsPluginLoader.server;
        this.pluginDir = pluginDir;
        this.mainJSFile = new File(pluginDir, "main.js");
        if (!mainJSFile.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                mainJSFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.pluginName = pluginDescription.getName();
        this.description = pluginDescription;
        this.logger = new PluginLogger(this);
        if (jsEngine == null) {
            jsEngine = Engine.newBuilder().allowExperimentalOptions(true).build();
        }
        jsContext = Context.newBuilder("js")
                .allowAllAccess(true)
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLoading(true)
                .allowHostClassLookup(className -> true)
                .engine(jsEngine)
                .option("js.nashorn-compat", "true")
                .build();
        this.initialized = true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public void onLoad() {
        try {
            jsContext.eval(Source.newBuilder("js", mainJSFile).build());
            jsGlobal = jsContext.getBindings("js");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        var mainFunc = jsGlobal.getMember("main");
        if (mainFunc != null && mainFunc.canExecute()) {
            mainFunc.executeVoid();
        }
        isEnabled = true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void onDisable() {
        var closeFunc = jsGlobal.getMember("close");
        if(closeFunc != null && closeFunc.canExecute()) {
            closeFunc.executeVoid();
        }
        isEnabled = false;
    }

    @Override
    public boolean isDisabled() {
        return !isEnabled;
    }

    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public PluginDescription getDescription() {
        return description;
    }

    @Override
    public InputStream getResource(String filename) {
        return null;
    }

    @Override
    public boolean saveResource(String filename) {
        return false;
    }

    @Override
    public boolean saveResource(String filename, boolean replace) {
        return false;
    }

    @Override
    public boolean saveResource(String filename, String outputName, boolean replace) {
        return false;
    }

    @Override
    public Config getConfig() {
        return null;
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void saveDefaultConfig() {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public PluginLogger getLogger() {
        return logger;
    }

    @Override
    public PluginLoader getPluginLoader() {
        return jsPluginLoader;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
