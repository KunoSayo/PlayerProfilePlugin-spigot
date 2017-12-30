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
import static yutang.yys.Configure.PlayerData.isDataExist;

import java.io.File;
import java.util.List;
/**
 * 我群鱼塘群号:617745343
 * @author 阴阳师
 */
public class PlayerProfilePlugin extends JavaPlugin{
	private CfgManager config;
	public String showItemListPath = "item.showitems";
	public final static String[] armorspaths = {"item.helmet","item.chest","item.legs", "item.boots"};

	@Deprecated
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
					case "help":{
						sender.sendMessage("-----------玩家资料帮助-----------");
						sender.sendMessage("/profile [玩家]      打开自己或其他玩家的个人资料");
						sender.sendMessage("/profile showitem    展示手上物品到个人资料上");
						sender.sendMessage("/profile showitems   查看已展示的物品的序号");
						sender.sendMessage("");
						return true;
					}
					case "showeditems":
					case "shownitems":
					case "showitems":{//查询展示的物品-------------------------------------------------------------------------------------------
						Player p;
						if(sender.hasPermission("playerprofile.op.queryshowitems")&&args.length==2){
							p = Bukkit.getPlayerExact(args[1]);
							if(p==null){
								p=(Player)Bukkit.getOfflinePlayer(args[1]);
							}
						}else{
							if(!(sender instanceof Player)){
								return false;
							}
							p=(Player)sender;
						}
						PlayerData pd = new PlayerData(this,p);
						List<ItemStack> items = pd.getShowedItems();
						int index = 0;
						for(ItemStack item : items) {
							sender.sendMessage(index + ":" + item.getType()+"，数量:"+item.getAmount());
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
							size = pd.getShowingItemCounts();
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
						int n = 2;
						Player p = null;
						OfflinePlayer op = null;
						if(sender.hasPermission("playerprofile.op.removeshowitem")){
							//获得玩家orElse离线玩家
							p=Bukkit.getPlayerExact(args[1]);
							if(p==null) {
								op = Bukkit.getOfflinePlayer(args[1]);
							}
						}
						if(p==null&&!isDataExist(this,op.getName())){
							if(!(sender instanceof Player)){
								sender.sendMessage("[Profile]未找到该玩家");
								return true;
							}
							p=(Player) sender;
							n=1;
						}
						final Player player = p;
						final OfflinePlayer offlinePlayer = op;
						p=null;op=null;
						PlayerData pd = p!=null?new PlayerData(this,player):new PlayerData(this,offlinePlayer);
						if(pd.getShowingItemCounts()==0){
							sender.sendMessage("该玩家并没有展示物品");
						}
						int indexes[] = new int[args.length-n];
						try{
							for(int index=0;n<args.length;n++,index++){
								int i = Integer.parseInt(args[n]);
								if(i>pd.getShowingItemCounts())
									continue;
								indexes[index]=i;
							}
						}catch(Exception e){
							return false;
						}
						new BukkitRunnable() {
							@Override
							public void run() {
								try {
									if (pd.removeShowItem(indexes).save()) {
										if (player != null) {
											sender.sendMessage("[Profile]成功移除"+pd.getOwnerName()+"的指定展示物品");
										}
									}
								} catch (Exception e) {
									if (player != null) {
										sender.sendMessage("[Profile]移除"+pd.getOwnerName()+"的展示物品失败");
									}
								}
							}
						}.runTaskAsynchronously(this);
						return true;
					}//removeitem case
					default:{
						if(sender instanceof Player){
							if(Bukkit.getPlayer(args[0])!=null){
								Player p = Bukkit.getPlayer(args[0]);
								PlayerData pd = new PlayerData(this,p);
								if(!pd.isDataExist()){
									sender.sendMessage("[Profile]没有找到该玩家或这是一个错误的子命令，输入/profile help来查看所有指令");
									return true;
								}
								if(!pd.cfg.getBoolean("config.show-profile")){
									sender.sendMessage("[Profile]"+p.getName()+"没有公开个人资料。");
									return true;
								}
								((Player) sender).openInventory(getPlayerProfileGui(p));
								return true;
							}else{
								if(Bukkit.getOfflinePlayer(args[0])!=null){
									OfflinePlayer p =Bukkit.getOfflinePlayer(args[0]);
									PlayerData pd = new PlayerData(this,p);
									if(!pd.isDataExist()){
										sender.sendMessage("[Profile]没有找到该玩家或这是一个错误的子命令，输入/profile help来查看所有指令");
										return true;
									}
									if(!pd.cfg.getBoolean("config.show-profile")){
										sender.sendMessage("[Profile]"+p.getName()+"没有公开个人资料。");
										return true;
									}
									((Player) sender).openInventory(getPlayerProfileGui(p));
									return true;
								}
							}
						}//玩家才能打开玩家个人资料 不听不听qwq
						sender.sendMessage("[Profile]这是一个错误的子命令，输入/profile help来查看所有指令");
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
