package com.shntec.bp.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.shntec.bp.common.ServerPushAdapter;
import com.shntec.bp.util.ShntecLogger;

public class ServerPushManager {

	HashMap <String, ServerPushAdapter> adapters = null;
	
	// Read/Write locker for adapters access
	private ReentrantReadWriteLock adaptersLocker = null;
	
	static private ServerPushManager instance = null;
	
	static public synchronized ServerPushManager getInstance() {

		if (null == instance ) {
			instance = new ServerPushManager();
		}
		
		return instance;
	}
	
	private ServerPushManager() {
		adapters = new HashMap <String, ServerPushAdapter>();
		adaptersLocker = new ReentrantReadWriteLock();
	}

	public void registerAdapter(ServerPushAdapter adapter) {
		
		String clientId = adapter.getClientId();
		
		if (adapters.containsKey(clientId)) {
			ShntecLogger.logger.info("Replace adpator for clientId:" + clientId);
			deregisterAdapter(adapter);
		}

		adaptersLocker.writeLock().lock();
		adapters.put(clientId, adapter);
		adaptersLocker.writeLock().unlock();

	}
	
	public void deregisterAdapter(ServerPushAdapter adapter) {
		
		String clientId = adapter.getClientId();


		if (adapters.containsKey(clientId)) {
			
			adaptersLocker.writeLock().lock();
			ServerPushAdapter oldAdapter = adapters.remove(clientId);
			adaptersLocker.writeLock().unlock();				
			oldAdapter.free();

		}
	}
	
	public void dispatchContent(String clientId, String content) {
		
		adaptersLocker.readLock().lock();

		// Find corresponding adapter
		ServerPushAdapter adapter = adapters.get(clientId);
		
		if (adapter != null) {
			adapter.pushContent(content);
		}

		adaptersLocker.readLock().unlock();

	}
	
	public void broadcastContent(String content) {

		Iterator<Entry<String, ServerPushAdapter>> iter = adapters.entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry<String, ServerPushAdapter> entry = iter.next(); 
		    String clientId = entry.getKey(); 
		    ServerPushAdapter adapter = entry.getValue(); 
		    if (clientId != null && adapter != null ) {
			    ShntecLogger.logger.debug("broadcastContent(): Find adapter for clientId: " + clientId);
				adapter.pushContent(content);
		    }
		} 
		
	}
	
	public int getServerPushStatus(String clientId){
		
		int adapterStatus = ServerPushAdapter.ADAPTER_STATUS_UNKNOWN;
		
		// Find adapter
		ServerPushAdapter adapter = adapters.get(clientId);
		
		if (adapter != null) {
			adapterStatus = adapter.getAdapterStatus();
			ShntecLogger.logger.debug(" " + adapterStatus);
		}
		else {
			adapterStatus = ServerPushAdapter.ADAPTER_STATUS_DISCONNECTED;
		}
		
		return adapterStatus;
		
	}

	public Date getLastSuccessHeartBeat(String clientId) {
		
		Date lastSuccessHeartBeat = null;
		
		// Find adapter
		ServerPushAdapter adapter = adapters.get(clientId);

		if (adapter != null) {
			lastSuccessHeartBeat = adapter.getLastSuccessHeartBeat();
		}

		return lastSuccessHeartBeat;
	}
}
