package com.example.safespace.entity


class Admin(val adminId: String, val name: String, val email: String, val icNum: String, val phoneNum: String, val status: String){
    constructor():this("","","","","","")
}

