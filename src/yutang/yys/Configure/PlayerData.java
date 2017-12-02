package yutang.yys.Configure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import yutang.yys.PlayerProfilePlugin;
/**
 * @author 阴阳师
 */
//config.XXXXX
public class PlayerData {
	private final PlayerProfilePlugin plugin;
	private final Player p;
	private final File file;
	public final FileConfiguration cfg;

	public static final String pShowItemsMaxPath = "config.show-item-max";

	public boolean initPlayerCfg(){
		cfg.addDefault(pShowItemsMaxPath, CfgManager.DshowmaxItems);
		cfg.addDefault("config.show-profile", CfgManager.Dshowprofile);
		cfg.addDefault("config.show-armors", CfgManager.Dshowarmors);
		cfg.addDefault("config.show-inventory", CfgManager.Dshowinventory);
		cfg.options().copyDefaults(true);
		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
			plugin.getLogger().info("初始化玩家"+p.getName()+"["+p.getUniqueId()+"]"+"数据失败");
			return false;
		}
		return true;
	}

	//不检查是不是List<ItemStack> start--------------------------------------------------------
	@SuppressWarnings("unchecked")
	public PlayerData addShowItem(ItemStack item){
		List<ItemStack> showeditems = new ArrayList<>();
		if(cfg.getList(plugin.showItemListPath)!=null){
			showeditems = (List<ItemStack>) cfg.getList(plugin.showItemListPath);
		}
		showeditems.add(item);
		cfg.set(plugin.showItemListPath, showeditems);
		return this;
	}
	@SuppressWarnings("unchecked")
	public PlayerData removeShowItem(int index) throws Exception{
		List<ItemStack> showeditems = (List<ItemStack>) cfg.getList(plugin.showItemListPath);
		showeditems.remove(index);
		cfg.set(plugin.showItemListPath, showeditems);
		return this;
	}
	@SuppressWarnings("unchecked")
	public PlayerData removeShowItem(int[] indexs) throws Exception{
		List<ItemStack> showeditems = (List<ItemStack>) cfg.getList(plugin.showItemListPath);
		for(int index:indexs){
			showeditems.remove(index);
		}
		cfg.set(plugin.showItemListPath, showeditems);
		return this;
	}
	@SuppressWarnings("unchecked")
	public List<ItemStack> getShowedItems() throws NullPointerException{
		List<ItemStack> showeditems = (List<ItemStack>) cfg.getList(plugin.showItemListPath);
		return showeditems;
	}
	//不检查是不是List<ItemStack> end----------------------------------------------------------

	public int getShowingItemCounts(){
	    try {
            return cfg.getList(plugin.showItemListPath).size();
        }catch(NullPointerException e){
	        return 0;
        }
	}

	public PlayerData(PlayerProfilePlugin plugin,Player p){
		this.plugin=plugin;
		this.p=p;
		this.file=new File(plugin.getDataFolder()+"\\PlayerDatas",p.getName()+".yml");
		this.cfg=load(file);
	}
	public FileConfiguration load(File file){
		if (!(file.exists())) { 
			try   
			{
				file.createNewFile();
			}
			catch(IOException   e)
			{
				e.printStackTrace();
			}
		}
		return YamlConfiguration.loadConfiguration(file);
	}
	public ItemStack[] getArmors(){
		ItemStack[] armors = new ItemStack[4];
		int n = 0;
		for(String path:PlayerProfilePlugin.armorspaths){
			armors[n]=cfg.getItemStack(path);
			n++;
		}
		return armors;
	}

	public PlayerData setItem(String path,ItemStack item){
		cfg.set(path, item);
		return this;
	}

	public PlayerData setItem(String[] paths,ItemStack[] items){
		for(int n=0;n<paths.length&&n<items.length;n++){
			cfg.set(paths[n], items[n]);
		}
		return this;
	}

	public PlayerData changeData(String[] paths,String[] args){
		for(int n=0;n<paths.length&&n<args.length;n++){
			cfg.set(paths[n], args[n]);
		}
		return this;
	}
	public PlayerData changeData(String path,String arg){
		cfg.set(path, arg);
		return this;
	}
	/**
	 * @author 阴阳师
	 * @return true 保存数据成功 |false 保存数据失败
	 */
	public boolean save(){
		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
			plugin.getLogger().info("保存玩家"+p.getName()+"["+p.getUniqueId()+"]"+"数据失败");
			return false;
		}
		return true;
	}
}
