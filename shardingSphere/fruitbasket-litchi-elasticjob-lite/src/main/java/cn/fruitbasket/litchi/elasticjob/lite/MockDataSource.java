package cn.fruitbasket.litchi.elasticjob.lite;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 假装这是MySQL或者Redis等数据源
 *
 * @author LiuBing
 * @since 2021/12/2
 */
@Component
public class MockDataSource {

    private static final int[] USER_IDS = IntStream.rangeClosed(1, 20).toArray();

    /**
     * 对用户Id取模，过滤得到跟分片ID匹配的结果
     *
     * @param shardingTotalCount 分片总数
     * @param shardingItem       分片
     * @return 用户Id列表
     */
    public List<Integer> getBy(int shardingTotalCount, int shardingItem) {
        return Arrays.stream(USER_IDS)
                .filter(userId -> userId % shardingTotalCount == shardingItem).boxed().collect(Collectors.toList());
    }
}
