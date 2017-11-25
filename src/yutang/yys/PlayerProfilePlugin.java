package yutang.yys;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import yutang.yys.Configure.CfgManager;
import yutang.yys.Configure.PlayerData;
import yutang.yys.Inventory.PlayerProfileInventoryClass;

import static yutang.yys.Inventory.PlayerProfileInventoryClass.getPlayerProfileGui;

import java.io.File;
import java.util.List;
/**
 * 我群鱼塘群号:617745343
 * @author 阴阳师
 */
public class PlayerProfilePlugin extends JavaPlugin{
	public CfgManager config;
	public String showItemListPath = "item.showitems";
	public final static String[] armorspaths = {"item.helmet","item.chest","item.legs", "item.boots"};

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		switch(cmd.getName().toLowerCase()){
		case "profile":{
			if(args.length==0){
				if(!(sender instanceof Player)){
					sender.sendMessage("[Profile]该指令仅支持玩家自己输入。");
					return true;
				}
				Player p = (Player)sender;
				p.openInventory(getPlayerProfileGui(p));
				return true;
			}
			//以上是纯/profile  下面看时搜索子命令
			switch(args[0].toLowerCase()){
			case "showitems":{
				if(!(sender instanceof Player)){
					return false;
				}
				Player p = (Player) sender;
				PlayerData pd = new PlayerData(this,p);
				List<ItemStack> items = pd.getShowedItems();
				int index = 0;
				for(ItemStack item : items){
					p.sendMessage(index+":"+item.getType());
					index++;
				}
				return true;
			}//showitems case
			case "showitem":{
				if(!(sender instanceof Player)){
					return false;
				}
				Player p = (Player) sender;
				if(p.getItemInHand().getType()==Material.AIR){
					p.sendMessage("[Profile]你想展示空气吗？？？？？？");
					return true;
				}
				PlayerData pd = new PlayerData(this,p);
				if(pd.cfg.getInt(PlayerData.pShowItemsMaxPath)!=0){
					int size;
					size = pd.getShowedItems()!=null?pd.getShowedItems().size():0;
					if(size>=pd.cfg.getInt(PlayerData.pShowItemsMaxPath)){
						sender.sendMessage("[Profile]你展示的物品超过限制了[已展示物品数:"+size+"你的限制"+pd.cfg.getInt(PlayerData.pShowItemsMaxPath)+"]");
						return true;
					}
				}else{
					sender.sendMessage("[Profile]你展示的物品超过限制了");
				}
				if(pd.addShowItem(p.getItemInHand()).save()){
					p.sendMessage("[Profile]新的展示物品添加成功");
				}else{
					p.sendMessage("[Profile]插件内部错误，请联系管理员查看后台。");
				}
				return true;
			}//showitem case
			case "removeitem":
			case "removeshowitem":{
				if(args.length==1){
					return false;
				}
				int n;
				Player p;
				if(sender.hasPermission("playerprofile.op.removeshowitem")){
					p =Bukkit.getPlayerExact(args[1])!=null?Bukkit.getPlayerExact(args[1])
							:(Player) Bukkit.getOfflinePlayer(args[1]);
					if(p==null){
						if(!(sender instanceof Player)){
							sender.sendMessage("[Profile]未找到该玩家");
							return true;
						}
						p=(Player) sender;
					}
					n=2;
				}else{
					if(!(sender instanceof Player)){
						sender.sendMessage("/profile removeshowitem <Player> <ItemType...>");
						return true;
					}
					p=(Player) sender;
					n=1;
				}
				final Player player = p;
				p=null;
				PlayerProfilePlugin plugin = this;
				PlayerData pd = new PlayerData(plugin,player);
				int indexs[] = new int[args.length-n];
				try{
					for(int index=0;n<args.length;n++,index++){

						indexs[index]=Integer.parseInt(args[n]);
					}
				}catch(Exception e){
					return false;
				}
				new BukkitRunnable() {
					@Override
					public void run() {
						try{
							if(pd.removeShowItem(indexs).save()){
								player.sendMessage("[Profile]成功移除展示物品");
							}
						}catch(Exception e){
							player.sendMessage("[Profile]移除展示物品失败，请确认是否存在该index");
						}
					}
				}.runTaskAsynchronously(plugin);
				return true;
			}//removeitem case
			default:{
				if(sender instanceof Player){
					if(Bukkit.getPlayer(args[0])!=null){
						Player p = Bukkit.getPlayer(args[0]);
						PlayerData pd = new PlayerData(this,p);
						if(!pd.cfg.getBoolean("config.show-profile")){
							sender.sendMessage("[Profile]"+p.getName()+"并没有公开个人资料。");
							return true;
						}
						((Player) sender).openInventory(getPlayerProfileGui(p));
						return true;
					}else{
						if(Bukkit.getOfflinePlayer(args[0])!=null){
							OfflinePlayer p =Bukkit.getOfflinePlayer(args[0]);
							PlayerData pd = new PlayerData(this,(Player)p);
							if(!pd.cfg.getBoolean("config.show-profile")){
								sender.sendMessage("[Profile]"+p.getName()+"并没有公开个人资料。");
								return true;
							}
							((Player) sender).openInventory(getPlayerProfileGui(p));
						}
					}
				}//玩家才能打开玩家个人资料 不听不听qwq
				sender.sendMessage("[Profile]没有找到该玩家或这是一个错误的子命令，输入/profile help来查看所有指令");
				return true;
			}//第一个参数default
			}//第一个参数switch
		}//指令profile case
		}//指令switch
		return false;
	}

	@Override//开启--------------------------------------------------------------------
	public void onEnable(){
		PlayerProfileListener.plugin=this;
		new PlayerProfileInventoryClass(this);
		this.config = new CfgManager(this);
		this.config.initConfig();
		getServer().getPluginManager().registerEvents(new PlayerProfileListener(), this);
	}

	@Override 
	public void onLoad(){
		File file = new File(getDataFolder()+"\\PlayerDatas");
		if(!file.exists()){
			file.mkdir();
		}
	}
}
