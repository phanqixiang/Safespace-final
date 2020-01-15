package com.example.safespace.entity


//class Message(val messageId: String, val chatSessionId: String, val senderId: String, val timestamp: String, val time: String, val message: String){
//    constructor():this("","","","","","")
//}

//class Message(val message:String){
 //   constructor():this("")
//}


class Message(val messageId: String, val messageTxt: String, val senderId: String, val receiverId: String, val timestamp:Long, val time: String){
    constructor():this("","","","",0,"")
}