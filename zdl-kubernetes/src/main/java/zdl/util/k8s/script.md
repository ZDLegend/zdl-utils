# kubernetes 命令行
### pod相关
* 查看所有pod运行情况: kubectl get pod
* 查看所有pod路由: kubectl get svc
* 进入某pod内: kubectl exec -it {podName} bash
* 删除pod（可以用来重启）: kubectl delete pod {podName}
* 实时查看pod日志: kubectl logs -f {podName}
* 编辑某个pod配置并重新加载: kubectl edit sts {serviceName}
* 获取某个pod配置信息: kubectl describe pod {podName}

### 文件
* copy： kubectl cp {podName}:{pod filePath + fileName} {local filePath + fileName} 

### 实用
* 在服务器上运行某个pod内的命令，不需要先进入pod内: kubectl exec -it {podName} -- {script}