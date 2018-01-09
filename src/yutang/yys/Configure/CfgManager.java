package yutang.yys.Configure;

import org.bukkit.configuration.file.FileConfiguration;
import yutang.yys.PlayerProfilePlugin;
/**\
 * 
 * @author 阴阳师
 *
 */
public class CfgManager {
	private final PlayerProfilePlugin plugin;
	private FileConfiguration config;
	public static int DshowmaxItems;
	public static boolean Dshowarmors;
	public static boolean Dshowprofile;
	public static boolean Dshowinventory;
	public CfgManager(PlayerProfilePlugin plugin){
		this.plugin=plugin;
		this.config=plugin.getConfig();
	}
	private final String showmaxpath = "Player-Default-Config.show-items-max";
	private final String showarmorpath = "Player-Default-Config.show-armor";
	private final String showprofilepath = "Player-Default-Config.show-profile";
	private final String showinventorypath = "Player-Default-Config.show-inventory-item";
	public void initConfig(){
		config.addDefault(showmaxpath, 9);
		config.addDefault(showarmorpath, true);
		config.addDefault(showprofilepath, true);
		config.addDefault(showinventorypath, true);
		config.options().copyDefaults(true);
		plugin.saveConfig();
		plugin.reloadConfig();
		config=plugin.getConfig();
		updateVars();
	}
	
	private void updateVars(){
		DshowmaxItems=config.getInt(showmaxpath);
		Dshowarmors=config.getBoolean(showarmorpath);
		Dshowprofile=config.getBoolean(showprofilepath);
		Dshowinventory=config.getBoolean(showinventorypath);
	}
	
	public void reloadConfig(){
		plugin.reloadConfig();
		config=plugin.getConfig();
		updateVars();
		plugin.getLogger().info("重载插件配置文件完毕");
	}
}
