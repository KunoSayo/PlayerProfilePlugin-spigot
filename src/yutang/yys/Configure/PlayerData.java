package yutang.yys.Configure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
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
	private final OfflinePlayer op;
	private final File file;
	private boolean exist;
	public final FileConfiguration cfg;

	public static final String pShowItemsMaxPath = "config.show-item-max";

	public boolean initPlayerCfg(){
		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		load(file);
		cfg.addDefault(pShowItemsMaxPath, CfgManager.DshowmaxItems);
		cfg.addDefault("config.show-profile", CfgManager.Dshowprofile);
		cfg.addDefault("config.show-armors", CfgManager.Dshowarmors);
		cfg.addDefault("config.show-inventory", CfgManager.Dshowinventory);
		cfg.options().copyDefaults(true);
		return save();
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
	public PlayerData removeShowItem(int[] indexes) {
		List<ItemStack> showeditems = (List<ItemStack>) cfg.getList(plugin.showItemListPath);
		for(int index:indexes){
			showeditems.remove(index);
		}
		cfg.set(plugin.showItemListPath, showeditems);
		return this;
	}
	@SuppressWarnings("unchecked")
	public List<ItemStack> getShowedItems() throws NullPointerException{
		return (List<ItemStack>) cfg.getList(plugin.showItemListPath);
	}
	//不检查是不是List<ItemStack> end----------------------------------------------------------
	public String getOwnerName(){
		return p!=null?p.getName():op.getName();
	}

	public boolean isDataInvalid(){
		return !exist;
	}

	public static boolean isDataInvalid(PlayerProfilePlugin plugin, String name){
		File file =new File(plugin.getDataFolder()+"\\PlayerDatas",name+".yml");
		return !file.exists();
	}

	public int getShowingItemCount(){
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
		exist= cfg!=null;
		this.op=null;
	}

	public PlayerData(PlayerProfilePlugin plugin, OfflinePlayer op){
		this.plugin=plugin;
		this.op=op;
		this.file=new File(plugin.getDataFolder()+"\\PlayerDatas",op.getName()+".yml");
		this.cfg=load(file);
		exist= cfg!=null;
		this.p=null;
	}

	private FileConfiguration load(File file){
		if (!(file.exists())) {
			return null;
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


	public PlayerData setItem(String[] paths,ItemStack[] items){
		for(int n=0;n<paths.length&&n<items.length;n++){
			cfg.set(paths[n], items[n]);
		}
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
			if(p!=null) {
				plugin.getLogger().info("保存玩家" + p.getName() + "[" + p.getUniqueId() + "]" + "数据失败");
			}else{
				plugin.getLogger().info("保存玩家" + op.getName() + "[" + op.getUniqueId() + "]" + "数据失败");
			}
			return false;
		}
		return true;
	}
}
