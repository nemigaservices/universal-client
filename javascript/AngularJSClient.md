# Kaazing JavaScript Universal Client for AngularJS
This library is intended to be used with AngularJS application; it provides AngularJS Service that can be used in the client application to interact with Kaazing Gateway.

## Using the Library
- Install library with the Bower as specified [README document][1].
- Add the following to the **\<head\>** section of your page:  
	```html
	
	<head>  
	....    
	<script src="bower_components/kaazing-javascript-universal-client/javascript/src/AngularUniversalClient.js"></script>
	<script src="bower_components/kaazing-javascript-universal-client/javascript/src/JmsClient.js"></script>
	....  
	</head>
	```
- Register KaazingClientService module with your main controller  
	```javascript
	angular.module("<your module name", 'KaazingClientService')
		.controller("<your controller name", function ($scope, ...,AngularUniversalClient) {  
	
	...  
	
	});
	```
- Establish connection within your controller
	```javascript
	angular.module("<your module name", 'KaazingClientService')
		.controller("<your controller name", function ($scope, ...,AngularUniversalClient) {  
	
	
		...  
	
	
		AngularUniversalClient.connect(protocol,url,username, password, topicP, topicS, noLocal, messageDestinationFuncHandle, loggerFuncHandle );  
		...  
	});
	```
	Where:
	- **protocol**: Specifies protocol that should be used for communications: jms - for communication with Kaazing JMS Gateway, amqp - for communication with Kaazing AMQP Gateway.
	- **url**: Connection URL (e.g. ws://localhost:8001/amqp or ws://localhost:8001/jms)
	- **username**: User name to be used to establish connection
	- **password**: User password to be used to establish connection
	- **topicP**: Name of the publishing endpoint - AMQP exchange used for publishing or JMS Topic
	- **topicS**: Name of the subscription endpoint - AMQP exchange used for subscription or JMS Topic
	- **noLocal**: Flag indicating whether the client wants to receive its own messages (true) or not (false). That flag should be used when publishing and subscription endpoints are the same.
	- **messageDestinationFuncHandle**: Function that will be used to process received messages from subscription endpoint in a format: _function(messageBody)_
	- **loggerFuncHandle**: function that is used for logging events in a format of function(severity, message)
- Add disconnect on window close (shown method uses JQuery):
	```javascript
	angular.module("<your module name", 'KaazingClientService')
		.controller("<your controller name", function ($scope, ...,AngularUniversalClient) {  
		...  
	
	
		AngularUniversalClient.connect(protocol,url,username, password, topicP, topicS, noLocal, messageDestinationFuncHandle, loggerFuncHandle );  
	
	
		...  
	
	
		$( window ).unload(function() {  
		    // Disconnect  
		    AngularUniversalClient.disconnect();  
		});  
	});
	```
- To send messages use AngularUniversalClient.sendMessage(msg)  
	where _**msg**_ is message to be sent. If _**msg**_ is not a string it will be converted to JSON.
	```javascript
	angular.module("<your module name", 'KaazingClientService')
		.controller("<your controller name", function ($scope, ...,AngularUniversalClient) {  
	
	
		...  
	
	
		AngularUniversalClient.connect(proitocol,url,username, password, topicP, topicS, noLocal, messageDestinationFuncHandle, loggerFuncHandle );  
	
	
		...  
	
	
		$scope.sendMessage = function(msg){  
		    //Send the message  
		    AngularUniversalClient.sendMessage(msg);  
	
	
		}  
	
	
		...  
	
	
		$( window ).unload(function() {  
		    // Disconnect  
		    AngularUniversalClient.disconnect();  
		});  
	});
	```
- When message is received, service will call a function registered as **messageDestinationFuncHandle** as shown above. E.g.  

	```javascript
	angular.module("<your module name", 'KaazingClientService')
		.controller("<your controller name", function ($scope, ...,AngularUniversalClient) {  
	
	
		...  
	
	
		$scope.processReceivedCommand=function(cmd){  
		    // Process received command  
		}  
	
	
		AngularUniversalClient.connect(protocol,url,username, password, topicP, topicS, noLocal,$scope.processReceivedCommand, loggerFuncHandle );  
	
	
		...  
	
	
		$scope.sendMessage = function(msg){  
		    //Send the message  
		    AngularUniversalClient.sendMessage(msg);  
		}  
	
	
		...  
	
	
		$( window ).unload(function() {  
		    // Disconnect  
		    AngularUniversalClient.disconnect();  
		});  
	});
	```
- To log WebSocket related events, specify the function as **loggerFuncHandle**. E.g.:  
	```javascript
	angular.module("\<your module name", 'KaazingClientService')
		.controller("<your controller name", function ($scope, ...,AngularUniversalClient) {  
		
		...  
		
		$scope.processReceivedCommand=function(cmd){  
		    // Process received command  
		}  
		
		$scope.logWebSocketMessage = function (cls, msg){    
		    // Log WebSocket message   
		}  
		
		AngularUniversalClient.connect(protocol,url,username, password, topicP, topicS, noLocal,$scope.processReceivedCommand, $scope.logWebSocketMessage );  
		
		...
		  
		$scope.sendMessage = function(msg){  
		    //Send the message  
		    AngularUniversalClient.sendMessage(msg);  
		}
		  
		
		...  
		
		$( window ).unload(function() {  
		    // Disconnect  
		    AngularUniversalClient.disconnect();  
		});  
	});
	```

## Organization of Kaazing JavaScript Universal Client for AngularJS  
![][image-1]

As shown on the diagram above, Kaazing AngularJS Universal Client works as following:
- Determine Client Library Facade based on the specified protocol
- Download all necessary JavaScript libraries including the needed Client Library Facade using RequireJS.
	- **Note** Due to certain limitations, RequireJS cannot download Kaazing JMSClient.js library - hence it has to be included in the \<head\> section
- Instantiate required Client Facade Library that will interact with necessary Kaazing Javascript Client Libraries
- Pass the data to and from the Kaazing Javascript Client libraries via instantiated Client Facade Library

For more information about Client Facade libraries see 
[AMQP Client Libraries Facade][2] and [JMS Client Libraries Facade][3].   

[1]:	README.md
[2]:	KaazingAMQPClientLibrariesFacade.md
[3]:	KaazingJMSClientLibrariesFacade.md
[image-1]:	images/AngularJSUniversalClient.png "AngularJS Universal Client"