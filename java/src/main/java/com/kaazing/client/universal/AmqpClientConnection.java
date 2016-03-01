/**
 * Kaazing Inc., Copyright (C) 2016. All rights reserved.
 */
package com.kaazing.client.universal;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.sql.Timestamp;

import com.kaazing.net.ws.amqp.AmqpChannel;
import com.kaazing.net.ws.amqp.AmqpProperties;

/**
 * Contains information specific to AMQP connections
 * @author romans
 *
 */
public class AmqpClientConnection extends ClientConnection {

	private AmqpChannel pubChannel;
	private AmqpChannel subChannel;
	private boolean opened=true;
	private String appId;
	private String userId;
	private String pubChannelName;
	private String queueName;

	public AmqpClientConnection(String connectionIdentifier, String appId, String userId, String pubChannelName, String queueName, AmqpChannel pubChannel, AmqpChannel subChannel) {
		super(connectionIdentifier);
		this.pubChannel=pubChannel;
		this.subChannel=subChannel;
		this.pubChannelName=pubChannelName;
		this.queueName=queueName;
		this.appId=appId;
		this.userId=userId;
	}

	@Override
	public void sendMessage(Serializable message) throws ClientException {
		byte[] serializedObject;
		try {
			serializedObject = Utils.serialize(message);
		} catch (IOException e) {
			throw new ClientException("Cannot serialize message "+message+" to send over connection "+this.getConnectionIdentifier(),e);
		}
		ByteBuffer buffer=ByteBuffer.allocate(serializedObject.length);
		buffer.put(serializedObject);
		buffer.flip();
		
		 Timestamp  ts = new Timestamp(System.currentTimeMillis());
         AmqpProperties props = new AmqpProperties();
         props.setMessageId("1");
         props.setCorrelationId("4");
         props.setAppId(appId);
         props.setUserId(userId);
         props.setPriority(6);
         props.setDeliveryMode(1);
         props.setTimestamp(ts);
         
         this.pubChannel.publishBasic(buffer, props, this.pubChannelName, AmqpUniversalClient.ROUTING_KEY, false, false);
         AmqpUniversalClient.LOGGER.debug("Sent message ["+message.toString()+"] to connection to "+this.getConnectionIdentifier());
	}

	@Override
	public void disconnect() throws ClientException {
		if (opened){
			AmqpUniversalClient.LOGGER.debug("Closing...");
			// TODO: Why the queues are not deleted??? 
			this.subChannel.purgeQueue(this.queueName,false);
			this.subChannel.deleteQueue(this.queueName, true, true, false);
			
			this.pubChannel.closeChannel(0, "", 0, 0);
			this.subChannel.closeChannel(0, "", 0, 0);
			opened=false;
		}
	}

}
