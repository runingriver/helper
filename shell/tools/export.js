db.auth("mongo","136313");
conn = new Mongo();
db = conn.getDB("sms");
var cur = db.myCollection.find({optime:{$gte: "2017-02-19 05:40:00", $lte: "2017-02-20 05:40:00"}});
var obj;
while(cur.hasNext()){
    obj = cur.next();
    print(obj.id+"\t"+obj.type+"\t"+obj.subaccount+"\n");
    print(obj.id+"\t"+obj.type+"\t"+obj.mobile+"\t"+obj.posttime+"\t"+obj.message+"\t"+obj.groupid+"\t"+obj.status+"\t"+obj.sent+"\t"+obj.optime+"\t"+obj.exectime+"\t"+obj.gid+"\t"+obj.retry+"\t"+obj.flag+"\t"+obj.channel+"\t"+obj.hostname+"\t"+obj.sendnum+"\t"+obj.md5+"\t"+obj.confirmtime+"\t"+obj.deliver+"\t\'NULL\'\t"+obj.numbertype+"\t"+obj.gwconfig+"\t"+obj.countrysign+"\t"+obj.sedeliver+"\t"+obj.ivr+"\t"+obj.voicestatus+"\t"+obj.push_id+"\t"+obj.identify_content+"\t"+obj.identify_type+"\t"+obj.identify_sub_type+"\t"+obj.signtype+"\t"+obj.autosign+"\t"+obj.ip+"\t"+obj.subaccount + "\t\'NULL\'\t\'NULL\'");
}