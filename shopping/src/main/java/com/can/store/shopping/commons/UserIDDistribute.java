package com.can.store.shopping.commons;

import com.can.store.shopping.commons.kizz.lib.utils.Func;

import java.security.PublicKey;
import java.util.Random;

public class UserIDDistribute {
    private static final long LIMIT = 100000000L;
    private long user_id;

    public static UserIDDistribute getInstance(){
        UserIDDistribute udd = new UserIDDistribute();
        udd.createUserID();
        return udd;
    }

    private void createUserID(){
        long time = System.currentTimeMillis()%LIMIT;
        if(time<0L){
            time = -time;
        }
        if(time/(LIMIT/10) < 1)
        {
            time = time*10;
        }
        Random r = new Random();
        int first = r.nextInt()%100;
        if(first<0){
            first = -first;
        }
        if(first/10<1){
            first = first*10;
        }
        StringBuffer sb = new StringBuffer();
        String uid = sb.append(first).append(time).toString();
        user_id = Func.toLong(uid);
    }

    public long getUser_id(){
        return this.user_id;
    }
}
