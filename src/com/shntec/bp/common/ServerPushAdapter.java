/**
 * 
 */
package com.shntec.bp.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.log4j.Logger;

import com.shntec.bp.json.JSONException;
import com.shntec.bp.json.JSONObject;
import com.shntec.bp.util.ShntecLogger;
import com.shntec.bp.util.SimpleDateParser;

/**
 * @author 1
 *
 */
public class ServerPushAdapter {
	
	// Default interval is 5 minutes
	public final static int DEFAULT_HEART_BEAT_INTERVAL = 300*1000;

	public final static int ADAPTER_STATUS_UNKNOWN = 0;
	// Adapter is working fine
	public final static int ADAPTER_STATUS_ALIVE = 1;	
	public final static int ADAPTER_STATUS_HEART_BEAT_SEND_FAILED = 2;
	public final static int ADAPTER_STATUS_PUSH_CONTENT_SEND_FAILED = 3;	
	public final static int ADAPTER_STATUS_DISCONNECTED = 4;
	private String clientId = null;
	
	private HttpServletResponse response = null;
	
	private PipedInputStream in = null;
	
	private PipedOutputStream out = null;

	private Logger logger = null;
	
	private Timer heartBeatTimer = null;
	private int heartBeatInterval = DEFAULT_HEART_BEAT_INTERVAL;
	
	private int adapterStatus = 0;
	private Date lastSuccessHeartBeat = null;

	TimerTask heartbeat = new TimerTask() {
		public void run() {
			// send back heart beat
			sendHeartBeat();
		}
	};
	
	private void startHeartBeatTimer() {
		heartBeatTimer = new Timer(true);
		heartBeatTimer.schedule(heartbeat, heartBeatInterval, heartBeatInterval);
	}
	
	private void stopHeartBeatTimer() {
		heartBeatTimer.cancel();
		heartBeatTimer.purge();
	}
	
	private void init() throws IOException {

		in = new PipedInputStream();
		out = new PipedOutputStream();
			
		in.connect(out);
		
		logger = Logger.getLogger(ShntecLogger.loggerName);
		
		startHeartBeatTimer();
		
	}
	
	public synchronized void free(){
		
		adapterStatus = ADAPTER_STATUS_DISCONNECTED;
		
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				// Ignore exception
			}
			finally {
				in = null;
			}
		}
		
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				// Ignore exception
			}
			finally {
				out = null;
			}
		}
		
		if (heartBeatTimer != null) {
			stopHeartBeatTimer();
		}
		
		if (response != null) {
			try {
				response.getOutputStream().close();
			} catch (IOException e) {
				// Ignore exception
			}
			finally {
				response = null;
			}
		}
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	public ServerPushAdapter() throws IOException {
		
		init();

	}
	
	public ServerPushAdapter(String clientId) throws IOException {
		
		init();
		
		this.clientId = clientId;

	}

	public ServerPushAdapter(String clientId, HttpServletResponse response) throws IOException {
		
		this.clientId = clientId;
		
		this.response = response;
		
		init();
	}

	public ServerPushAdapter(String clientId, HttpServletResponse response, int interval) throws IOException {
		
		this.clientId = clientId;
		
		this.response = response;
		
		this.heartBeatInterval = interval;
		
		init();
	}

	public String getClientId() {
		return clientId;
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}
	
	// Content source use this method to push content need to be delivered, more than 
	// one source may be exist simultaneously.
	public synchronized void pushContent(String content) {

		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
		
			bw.write(content);
			bw.write(System.getProperty("line.separator"));
			bw.flush();
		} catch (IOException e) {
			ShntecLogger.logger.error("Internal pipe broken");
			ShntecLogger.logger.error(e.getMessage());
		}
		
	}
	
	// Dispatch content through response channel
	private synchronized void dispatchContent(String content) throws Exception {

		if (response != null) {
			BufferedWriter out = new BufferedWriter(
					new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
			String msg = content + System.getProperty("line.separator");
			//PrintWriter out = response.getWriter();
			out.write(msg);
			out.flush();
		}
	}

	// Block and wait incoming content from source, running in the thread
	public void waitContent() {
		
		logger.debug("clientId=" + clientId 
				+ ", start to wait for incoming push content.");
		
		String content = new String();
		
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(in));
	
			// Enter waiting loop
			while (null != (content = br.readLine())) {
				logger.debug("clientId=" + clientId 
						+ ", received push content:" + content);
				dispatchContent(content);
			}
		}
		catch (ClientAbortException e) {
			ShntecLogger.logger.debug("clientId=" + getClientId()
					+ ", server push loop finished, peer reset.");
			ShntecLogger.logger.debug(e.getMessage());
		} 
		catch (Exception e) {
			ShntecLogger.logger.error("clientId=" + getClientId()
						+ ", server push loop finished, unhandled exception.");
			ShntecLogger.logger.error(e.getMessage());
		}		
		finally {
			logger.debug("clientId=" + clientId 
					+ ", End of waiting for incoming push content.");
			adapterStatus = ADAPTER_STATUS_PUSH_CONTENT_SEND_FAILED;
			free();
		}
	}
	
	private void sendHeartBeat() {
		
		Date now = new Date();
		
		ResponseMessageGenerator generator = new ResponseMessageGenerator();
		JSONObject payload = new JSONObject();
		
		try {
			payload.put("hbt", SimpleDateParser.format(now));
			generator.generateSuccessResponse(
					JSONMessageConstant.ACTION_CODE_SERVER_PUSH_CONTENT, payload);
		} catch (JSONException e) {
			logger.error("Generate heart beat message failed.");
			logger.error(e.getMessage());
			generator.generateErrorResponse(JSONMessageConstant.ACTION_CODE_SERVER_PUSH_CONTENT,
					ShntecErrorCode.SHNTEC_ERROR_CODE_SYSTEM_ERROR, "Generate heart beat message failed.");
		}
		
		try {
			// Use dispatch method to send heart beat directly.
			dispatchContent(generator.generate());
			lastSuccessHeartBeat = now;
			adapterStatus = ADAPTER_STATUS_ALIVE;
		} catch (Exception e) {
			logger.error("Dispatch heart beat to client failed.");
			logger.error(e.getMessage());
			adapterStatus = ADAPTER_STATUS_HEART_BEAT_SEND_FAILED;
			free();
		}		
	}

	public void sendCallbackAction(int actionCode){
		
		ResponseMessageGenerator generator = new ResponseMessageGenerator();
		JSONObject payload = new JSONObject();
		
		try {
			payload.put("cba", actionCode);
			generator.generateSuccessResponse(
					JSONMessageConstant.ACTION_CODE_SERVER_PUSH_CONTENT, payload);
		} catch (JSONException e) {
			logger.error("Generate callback action message failed.");
			logger.error(e.getMessage());
			generator.generateErrorResponse(JSONMessageConstant.ACTION_CODE_SERVER_PUSH_CONTENT,
					ShntecErrorCode.SHNTEC_ERROR_CODE_SYSTEM_ERROR, "Generate callback action message failed.");
		}
		
		pushContent(generator.generate());
	}
	
	public void sendFinishServerPush() {

		ResponseMessageGenerator generator = new ResponseMessageGenerator();
		generator.generateSuccessResponse(
				JSONMessageConstant.ACTION_CODE_FINISH_SERVER_PUSH, null);
		
		pushContent(generator.generate());
	}
	
	public int getAdapterStatus() {
		
		return adapterStatus;

	}
	
	public Date getLastSuccessHeartBeat() {
		
		return this.lastSuccessHeartBeat;
		
	}

}
