package yutang.yys;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import yutang.yys.Configure.PlayerData;

public class PlayerProfileListener implements Listener {
	public static PlayerProfilePlugin plugin;
	
	@EventHandler
	public void onClick(InventoryClickEvent e){
		if(!e.getInventory().getTitle().endsWith("的个人资料")){
			return;
		}
		e.setCancelled(true);
		if(e.getRawSlot()==8){
			e.getWhoClicked().closeInventory();
		}
	}

	@EventHandler
	public void updatePlayerProfile(InventoryCloseEvent e){
		Player p=(Player) e.getPlayer();
		PlayerInventory inv =p.getInventory();
		PlayerData pd = new PlayerData(plugin,p);
		ItemStack[] armors = new ItemStack[]{inv.getHelmet(),inv.getChestplate(),inv.getLeggings(),inv.getBoots()};
		new BukkitRunnable() {
			@Override
			public void run() {
				pd.setItem(PlayerProfilePlugin.armorspaths, armors).save();
			}
		}.runTaskAsynchronously(plugin);
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		new BukkitRunnable() {
			
			@Override
			public void run() {
				PlayerData pd =  new PlayerData(plugin,p);
				pd.initPlayerCfg();
			}
		}.runTaskAsynchronously(plugin);
	}
}
