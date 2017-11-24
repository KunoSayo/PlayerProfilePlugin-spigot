package yutang.yys.Inventory;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import yutang.yys.PlayerProfilePlugin;
import yutang.yys.Configure.PlayerData;
/**
 * @author 阴阳师
 */
public class PlayerProfileInventoryClass {
	private static  PlayerProfilePlugin plugin;
	/**
	 * @param plugin
	 */
	public PlayerProfileInventoryClass(PlayerProfilePlugin plugin){
		PlayerProfileInventoryClass.plugin=plugin;
	}
	/**
	 * @author 阴阳师
	 * @param CommandSender
	 * @param p //资料主人
	 * @return 背包
	 */
	//00 01 02 03 04 05 06 07 08
	//09 10 11 12 13 14 15 16 17
	//18 19 20 21 22 23 24 25 26
	//27 28 29 30 31 32 33 34 35
	public static Inventory getPlayerProfileGui(Player p){
		int slot = 1;
		PlayerData pd = new PlayerData(plugin,p);
		boolean showinventory=pd.cfg.getBoolean("config.show-inventory");
		int extralines=0;
		if(showinventory){
			extralines=5*9;
		}else{
			@SuppressWarnings("unchecked")
			List<ItemStack> showeditems = (List<ItemStack>) pd.cfg.getList(plugin.showItemListPath);
			if(showeditems!=null){
				int a = showeditems.size();
				if(a>9&&a<=18){
					extralines=3;
				}//1~9
				if(a>18&&a<=27){
					extralines=4;
				}
				if(a>27&&a<=36){
					extralines=5;
				}
			}
		}
		Inventory inv = Bukkit.createInventory(p, 9+extralines, "玩家"+p.getName()+"的个人资料");
		if(pd.cfg.getBoolean("config.show-armors")){
			for(int n=0;n<getPlayerArmors(p).length;n++,slot++){
				inv.setItem(slot, getPlayerArmors(p)[n]);
			}
		}
		slot = 18;
		if(showinventory){
			Inventory pinv = p.getInventory();
			for(int iS/*inventory Slot*/ = 0;iS<36;iS++,slot++){
				inv.setItem(slot,(pinv.getItem(iS)));
			}
		}
		{
			ItemStack skull = new ItemStack(Material.SKULL_ITEM ,1,(short)3);
			ItemMeta skullm = skull.getItemMeta();
			skullm.setDisplayName(p.getName());
			skull.setItemMeta(skullm);
			//			net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(skull);
			//			NBTTagCompound nbt = (nmsItem.hasTag()) ? nmsItem.getTag(): new NBTTagCompound();
			//			{
			//				NBTTagList modifiers = new NBTTagList();
			//				NBTTagCompound skullOwnerID = new NBTTagCompound();
			//				{
			//					skullOwnerID.set("ID", new NBTTagString(p.getUniqueId().toString()));
			//				}
			//				nbt.set("Owner", modifiers);
			//				nbt.set("Name", new NBTTagString(p.getName()));
			//			}
			//			nmsItem.setTag(nbt);
			//			ItemStack hasNBTItem = CraftItemStack.asBukkitCopy(nmsItem);
			//			p.getInventory().setItem(0, hasNBTItem);
			inv.setItem(0, skull);
		}
		{
			ItemStack close = new ItemStack(Material.BARRIER,1);
			ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)0);
			ItemMeta closem = close.getItemMeta();
			ItemMeta emptym = empty.getItemMeta();
			closem.setDisplayName("关闭资料界面");
			emptym.setDisplayName("");
			close.setItemMeta(closem);
			empty.setItemMeta(emptym);
			inv.setItem(8, close);
			for(slot=9;slot<18;slot++){
				inv.setItem(slot, empty);
			}//运行完毕 slot=18
		}
		if(!showinventory){
			@SuppressWarnings("unchecked")
			List<ItemStack> showeditems = (List<ItemStack>) pd.cfg.getList(plugin.showItemListPath);
			if(showeditems!=null){
				for(ItemStack item : showeditems){
					inv.setItem(slot, item);
					slot++;
				}
			}
		}
		return inv;
	}

	/**
	 * @author 阴阳师
	 * @param CommandSender
	 * @param p //资料主人
	 * @return 背包
	 */
	public static Inventory getPlayerProfileGui(final OfflinePlayer op){
		int slot = 3;
		int extralines=0;
		Player p = (Player)op;
		PlayerData pd = new PlayerData(plugin,p);
		@SuppressWarnings("unchecked")
		List<ItemStack> showeditems = (List<ItemStack>) pd.cfg.getList(plugin.showItemListPath);
		if(showeditems!=null){
			int a = showeditems.size();
			if(a>9&&a<=18){
				extralines=3;
			}//1~9
			if(a>18&&a<=27){
				extralines=4;
			}
			if(a>27&&a<=36){
				extralines=5;
			}
		}
		Inventory inv = Bukkit.createInventory(p, 9+extralines*9, "玩家"+p.getName()+"的个人资料");
		{
			ItemStack skull = new ItemStack(Material.SKULL ,1,(short)3);
			ItemMeta skullm = skull.getItemMeta();
			skullm.setDisplayName(p.getName());
			skull.setItemMeta(skullm);
			inv.setItem(0, skull);

			ItemStack pinfo = new ItemStack(Material.PAPER ,1);
			inv.setItem(1, pinfo);
		}
		if(pd.cfg.getBoolean("config.show-armors")){
			for(int n=0;n<pd.getArmors().length;n++,slot++){
				inv.setItem(slot, pd.getArmors()[n]);
			}
		}
		if(showeditems!=null){
			{
				ItemStack close = new ItemStack(Material.BARRIER,1);
				ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)0);
				ItemMeta closem = close.getItemMeta();
				ItemMeta emptym = empty.getItemMeta();
				closem.setDisplayName("关闭资料界面");
				emptym.setDisplayName("");
				close.setItemMeta(closem);
				empty.setItemMeta(emptym);
				inv.setItem(8, close);
				for(slot=9;slot<18;slot++){
					inv.setItem(slot, empty);
				}//运行完毕 slot=18
			}
			for(ItemStack item : showeditems){
				inv.setItem(slot, item);
				slot++;
			}
		}
		return inv;
	}

	private static ItemStack[] getPlayerArmors(Player p){
		return new ItemStack[]{p.getInventory().getHelmet()
				,p.getInventory().getChestplate()
				,p.getInventory().getLeggings()
				,p.getInventory().getBoots()};
	}
}
