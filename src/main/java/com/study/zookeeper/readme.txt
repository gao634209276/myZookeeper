ZooKeeper API 共包含 5 个包，分别为：
 org.apache.zookeeper ，
  org.apache.zookeeper.data ，
  org.apache.zookeeper.server ，
   org.apache.zookeeper.server.quorum 
   和org.apache.zookeeper.server.upgrade 。
   其中 org.apache.zookeeper 包含 ZooKeeper 类，它我们编程时最常用的类文件。

这个类是 ZooKeeper 客户端库的主要类文件。
如果要使用 ZooKeeper 服务，应用程序首先必须创建一个Zookeeper 实例，这时就需要使用此类。
一旦客户端和 ZooKeeper 服务建立起连接， ZooKeeper 系统将会分配给此连接回话一个 ID 值，
并且客户端将会周期地向服务器发送心跳来维持会话的连接。
只要连接有效，客户端就可以调用 ZooKeeper API 来做相应的处理。
它提供了表 1 所示几类主要方法 ：
create在本地目录树中创建一个节点
delete删除一个节点
exists测试本地是否存在目标节点
get/set data从目标节点上读取 / 写数据
get/set ACL获取 / 设置目标节点访问控制列表信息
get children检索一个子节点上的列表
sync等待要被传送的数据
此类包含两个主要的 ZooKeeper 函数，分别为 createZKInstance （）和 ZKOperations （）。
其中createZKInstance （）函数负责对 ZooKeeper 实例 zk 进行初始化。
 ZooKeeper 类有两个构造函数，我们这里使用
 ZooKeeper（String connectString, int sessionTimeout, Watcher watcher）对其进行初始化。
因此，我们需要提供初始化所需的，连接字符串信息，会话超时时间，以及一个 watcher 实例。
 17 行到 23 行代码，是程序所构造的一个 watcher 实例，它能够输出所发生的事件。
ZKOperations （）函数是我们所定义的对节点的一系列操作。
它包括：
创建 ZooKeeper 节点（ 33 行到 34 行代码）、
查看节点（ 36 行到 37 行代码）、
修改节点数据（ 39 行到 40 行代码）、
查看修改后节点数据（ 42 行到43 行代码）、
删除节点（ 45 行到 46 行代码）、
查看节点是否存在（ 48 行到 49 行代码）。
另外，需要注意的是：
在创建节点的时候，需要提供节点的名称、数据、权限以及节点类型。
此外，使用 exists 函数时，如果节点不存在将返回一个 null 值。
关于 ZooKeeper API 的更多详细信息，读者可以查看 ZooKeeper 的 API 文档，如下所示：

