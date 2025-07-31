

function FinsembleServer(){
  var that=this;
  if(parent && parent.window && parent.window.FSBL)
    window.FSBL=parent.window.FSBL;
  if (window.FSBL && FSBL.addEventListener) {
    FSBL.addEventListener('onReady', function(){that.initFinsemble();});
  } else {
    window.addEventListener('FSBLReady',  function(){that.initFinsemble();});
    if(parent && parent.window)
      parent.window.addEventListener('FSBLReady',  function(){window.FSBL=parent.window.FSBL;that.initFinsemble();});
  }

  if(parent && parent.window && parent.window.fdc3)
	  window.fdc3=parent.window.fdc3;
  if(window.fdc3){
	  that.initFDC3();
  } else {
	  window.addEventListener("fdc3Ready", function(){ that.initFDC3(); } ); 
	  if(parent && parent.window)
		  parent.window.addEventListener("fdc3Ready", function() { window.fdc3=parent.window.fdc3; that.initFDC3(); });

  }
}

FinsembleServer.prototype.finsembleInit;
FinsembleServer.prototype.fdc3Init;
FinsembleServer.prototype.channel;

FinsembleServer.prototype.initFDC3=function(){
	this.fdc3Init=true;
	if(this.peer != null)
		this.sendOnInit();
}

FinsembleServer.prototype.initFinsemble=function(){
  this.finsembleInit=true;
  if(this.peer!=null)
    this.sendOnInit();
}
FinsembleServer.prototype.checkInit=function(){
  if(!this.finsembleInit){
    this.peer.sendToServer('onError','sendMessageToFSBL','FSBL not initialized');
    return false; 
  }
  return true;
}


FinsembleServer.prototype.getOrCreateChannel=function(channelId){
	
}

FinsembleServer.prototype.registerAmiGuiServicePeer=function(peer){
  this.peer=peer;
  if(this.finsembleInit)
    this.sendOnInit();
}
FinsembleServer.prototype.sendOnInit=function(peer){
  if(this.finsembleInit)
	  this.peer.sendToServer('onInit');
  if(this.fdc3Init == true)
	  this.peer.sendToServer('onInitFdc3', {initFdc3:this.fdc3Init});
  
}

FinsembleServer.prototype.bringToFront=function(){
	window.finsembleWindow && window.finsembleWindow.bringToFront();
	window.finsembleWindow && window.finsembleWindow.focus();
}
FinsembleServer.prototype.moveWindowTo=function(windowId,x,y){
	var w=portletManager.getWindow(windowId);
	if(w && w.moveWindowTo)
	  w.moveWindowTo(x,y);
}
FinsembleServer.prototype.maximizeWindow=function(windowId){
	var w=portletManager.getWindow(windowId);
	if(w && w.maximize)
	  w.maximize();
}
FinsembleServer.prototype.unmaximizeWindow=function(windowId){
	var w=portletManager.getWindow(windowId);
	if(w && w.unmaximize)
	  w.unmaximize();
}

FinsembleServer.prototype.getOrCreateChannel=async function(channelId){
	if (this.checkInit()==false) {
		return;
	}
	try{
		if (!this.channel || this.channel.id != channelId) {
		   this.channel= await fdc3.getOrCreateChannel(channelId);
	  	}
	  	this.peer.sendToServer('onGetChannel',this.channel);
	} catch (err) {
		this.peer.sendToServer('onError','getOrCreateChannel', err);
	  	return;
	}
}

// broadcast this context to system channels
FinsembleServer.prototype.broadcast=async function(context){
	if (this.checkInit()==false) {
		return;
	}
	try {
		var res = await fdc3.broadcast(context);
  	} catch (err){
  		this.peer.sendToServer('onError','broadcast', err);
  		return;
  	}
}
FinsembleServer.prototype.broadcastChannel=async function(channelId, data){
	if (this.checkInit()==false) {
		return;
	}
	try {
		if (!this.channel) {
		  this.channel = await fdc3.getOrCreateChannel(channelId);
		}
	  	var res = await this.channel.broadcast(data);
	} catch (err) {
		this.peer.sendToServer('onError','broadcastChannel', err);
    	return;
	}
}
// contextType String
FinsembleServer.prototype.addContextListener=async function(contextType){
	if (this.checkInit()==false) {
		return;
	}
	var that = this;
	try {
	  var res = await fdc3.addContextListener(contextType, (context, metadata) => {
			  that.peer.sendToServer('onContext',context,metadata);
		  }
	  );
	} catch (err) {
	  this.peer.sendToServer('onError','addContextListener', err);
	  return;
	}
}

// let user decide which app to use
FinsembleServer.prototype.raiseIntent=async function(intent, context){
	if (this.checkInit()==false) {
		return;
	}
	try {
		var intentResolution=await fdc3.raiseIntent(intent, context);
		const result = await intentResolution.getResult();
		if (result) {
			this.peer.sendToServer('onRaiseIntent', result);
		} else {
			this.peer.sendToServer('onRaiseIntent', intentResolution);
		}
	}catch (err) {
		this.peer.sendToServer('onError','raiseIntent', err);
		return;
	}
}

FinsembleServer.prototype.raiseIntentForContext=async function(context){
	if (this.checkInit()==false) {
		return;
	}
	try {
		const intentResolution = await fdc3.raiseIntentForContext(context);
		const result = await intentResolution.getResult();
		if (result) {
			this.peer.sendToServer('onRaiseIntentForContext', result);
		} else {
			this.peer.sendToServer('onRaiseIntentForContext',intentResolution);
		}
	}catch (err) {
		this.peer.sendToServer('onError','raiseIntentForContext', err);
		return;
	}
}
// contextType String
FinsembleServer.prototype.addContextListenerChannel=async function(channelId, contextType){
	if (this.checkInit()==false) {
		return;
	}
	var that = this;
	try {
		if (this.channel==null) {  
			this.channel = await fdc3.getOrCreateChannel(channelId);
		}
  	var res = await this.channel.addContextListener(contextType, 
		(context, metadata) => {
        	that.peer.sendToServer('onContext',context,metadata);
  		}
  	);

  } catch (err) {
    this.peer.sendToServer('onError','addContextListenerChannel', err);
  }
}

FinsembleServer.prototype.sendMessage=function(channel,data){
	if (this.checkInit()==false) {
		return;
	}
	FSBL.Clients.RouterClient.transmit(channel,data);
}

FinsembleServer.prototype.addListener=function(channel){
	if (this.checkInit()==false) {
		return;
	}
	var that=this;
	FSBL.Clients.RouterClient.addListener(channel, function(error,response) {
		if (error) {
			that.peer.sendToServer('onError','addlistener','for channel '+channel+': '+JSON.stringify(error));
		}else{
			that.peer.sendToServer('onMessage',channel,response);
		}
	}
  );
}
