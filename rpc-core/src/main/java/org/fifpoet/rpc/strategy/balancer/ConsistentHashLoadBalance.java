package org.fifpoet.rpc.strategy.balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import org.fifpoet.entity.RpcRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConsistentHashLoadBalance implements LoadBalancer {
    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();
    @Override
    public Instance select(List<Instance> instances, RpcRequest request) {

        int identityHashCode = System.identityHashCode(instances);
        // build rpc service name by rpcRequest
        String rpcServiceName = request.getServiceName();
        ConsistentHashSelector selector = selectors.get(rpcServiceName);
        // check for updates
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(rpcServiceName, new ConsistentHashSelector(instances, 160, identityHashCode));
            selector = selectors.get(rpcServiceName);
        }
        return selector.select(rpcServiceName + Arrays.stream(request.getParameters()));
    }

    static class ConsistentHashSelector {
        private final TreeMap<Long, Instance> virtualInstance;
        private final int identityHashCode;
        public ConsistentHashSelector(List<Instance> instances, int replicaNumber, int identityHashCode) {
            this.virtualInstance = new TreeMap<>();
            this.identityHashCode = identityHashCode;
            for (Instance instance : instances) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(instance.getIp() + instance.getPort() + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualInstance.put(m, instance);
                    }
                }
            }
        }

        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24 | (long) (digest[2 + idx * 4] & 255) << 16 | (long) (digest[1 + idx * 4] & 255) << 8 | (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        public Instance select(String rpcServiceKey) {
            byte[] digest = md5(rpcServiceKey);
            return selectForKey(hash(digest, 0));
        }

        public Instance selectForKey(long hashCode) {
            Map.Entry<Long, Instance> entry = virtualInstance.tailMap(hashCode, true).firstEntry();
            if (entry == null) {
                entry = virtualInstance.firstEntry();
            }
            return entry.getValue();
        }
    }
}
