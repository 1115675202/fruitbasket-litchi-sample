package cn.fruitbasket.litchi.elasticjob.lite;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author LiuBing
 * @since 2021/12/2
 */
@Slf4j
@Component
public class UserDataflowJob implements DataflowJob<Integer> {

    @Autowired
    private MockDataSource dataSource;

    @Override
    public List<Integer> fetchData(final ShardingContext shardingContext) {
        return dataSource.getBy(shardingContext.getShardingTotalCount(), shardingContext.getShardingItem());
    }

    @Override
    public void processData(final ShardingContext shardingContext, final List<Integer> data) {
        log.info("分片总数={}，当前分片值={}，分片匹配的用户ID={}"
                , shardingContext.getShardingTotalCount()
                , shardingContext.getShardingItem()
                , data);
        // 执行业务逻辑
    }
}
