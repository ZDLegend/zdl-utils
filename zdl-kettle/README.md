## kettle 相关说明
* kettle环境搭建
Pentaho官方仓库：
 > https://nexus.pentaho.org/content/groups/omni

在maven中的setting.xml添加相关配置（example:resource/maven/setting.xml），才能成功导入pentaho-kettle相关jar包

* kettle版本8.0之后配置集群分区运行出现空指针异常，已有issue:
 > https://jira.pentaho.com/browse/PDI-18333?jql=text%20~%20%22partition%20cluster%22%20ORDER%20BY%20created%20DESC

是由于新版本服务端代码变动。
例如： step A 为输入，step B 为集群process，之前我们需要对step B添加partition分区操作，而现在，我们需要对step A添加partition分区操作
