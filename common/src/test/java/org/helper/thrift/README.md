## thrift使用
1. 先create一个thrift定义文件
2. 按照thrift规范写一个thrift文件
3. thrift --gen java account.thrift 
随后会在当前目录生成gen-java目录，该目录下会按照namespace定义的路径名一次一层层生成文件夹,然后是生成的代码
Tip: gen-java目录下的文件夹会被复制到项目中, 所以namespace的定义要按照项目的package结构来. 当然如果不想这样,放在最外层也可以!
4. 编写service和client代码, java中首先要引入libthrift依赖
Tip: libthrift依赖要根据版本来, 0.9对应0.9, 0.11对应0.11
5. 其他的看代码吧