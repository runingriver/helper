db.auth("mongo","xxx");
var cur = db.myCollection.find({"num":20170704,"deliver":"DELIVRD"});
var obj;
var count = 0;
while(cur.hasNext()){
    obj = cur.next();
    count +=obj.sendnum;
}

print("total send success:" + count);