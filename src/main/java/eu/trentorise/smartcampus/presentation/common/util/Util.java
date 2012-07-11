package eu.trentorise.smartcampus.presentation.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.introspect.NopAnnotationIntrospector;

import eu.trentorise.smartcampus.presentation.data.BasicObject;
import eu.trentorise.smartcampus.presentation.data.SyncData;
import eu.trentorise.smartcampus.presentation.data.SyncDataRequest;
import eu.trentorise.smartcampus.presentation.storage.sync.mongo.SyncObjectBean;

public class Util {

    private static ObjectMapper fullMapper = new ObjectMapper();
    static {
        fullMapper.setAnnotationIntrospector(NopAnnotationIntrospector.nopInstance());
        fullMapper.getDeserializationConfig().set(DeserializationConfig.Feature.READ_ENUMS_USING_TO_STRING, true);
        fullMapper.getDeserializationConfig().set(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        fullMapper.getDeserializationConfig().set(DeserializationConfig.Feature.READ_ENUMS_USING_TO_STRING, true);

        fullMapper.getSerializationConfig().set(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING, true);
        fullMapper.getSerializationConfig().set(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    }

    @SuppressWarnings("unchecked")
	public static SyncDataRequest convertRequest(Map<String,Object> requestMap, long since) throws ClassNotFoundException {
		SyncData data = new SyncData();
		try {
			data.setVersion(Long.parseLong(requestMap.get("version").toString()));
		} catch (Exception e1) {
			data.setVersion(0);
		}
		Map<String,Object> readMap = (Map<String,Object>)requestMap.get("updated");
		Map<String, List<BasicObject>> updatedMap = new HashMap<String, List<BasicObject>>();

		for (String key : readMap.keySet()) {
			Class<? extends BasicObject> cls = (Class<? extends BasicObject>) Thread.currentThread().getContextClassLoader().loadClass(key);
			List<Object> list = (List<Object>)readMap.get(key);
			List<BasicObject> result = new ArrayList<BasicObject>();
			if (list != null) {
				for (Object o : list) result.add(fullMapper.convertValue(o, cls));
			}
			updatedMap.put(key, result);
		}
		data.setUpdated(updatedMap);
		
		Map<String,List<String>> deleted = (Map<String,List<String>>)requestMap.get("deleted");
		data.setDeleted(deleted);
    	return new SyncDataRequest(data, since);
    } 
    
    public static <T> T convert(Object object, Class<T> cls) {
    	return fullMapper.convertValue(object, cls);
    }
    
    public static SyncObjectBean convertToSyncObjectBean(BasicObject o) {
    	SyncObjectBean result = new SyncObjectBean();
    	@SuppressWarnings("unchecked")
		Map<String,Object> map = convert(o, Map.class);
    	result.setContent(map);
    	result.setDeleted(false);
    	result.setId(o.getId());
    	result.setType(o.getClass().getCanonicalName());
    	result.setUpdateTime(o.getUpdateTime() < 0 ? System.currentTimeMillis() : o.getUpdateTime());
    	result.setVersion(o.getVersion());
    	result.setUser(o.getUser());
    	return result;
    }
    
	public static <T extends BasicObject> T convertSyncBeanToBasicObject(SyncObjectBean bean, Class<T> cls) {
    	return convert(bean.getContent(), cls);
    }
}
