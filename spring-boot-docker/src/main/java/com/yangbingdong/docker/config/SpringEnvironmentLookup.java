package com.yangbingdong.docker.config;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.AbstractLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static com.yangbingdong.springbootcommon.utils.StringUtil.isBlank;
import static com.yangbingdong.springbootcommon.utils.StringUtil.isNotBlank;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * @author ybd
 * @date 18-4-9
 * @contact yangbingdong1994@gmail.com
 */
@SuppressWarnings("unused")
@Plugin(name = "spring", category = StrLookup.CATEGORY)
public class SpringEnvironmentLookup extends AbstractLookup {
	private static LinkedHashMap profileYmlData;
	private static LinkedHashMap metaYmlData;
	private static boolean profileExist;
	private static Map<String, String> map = new HashMap<>(16);
	private static final String META_PROFILE = "application.yml";
	private static final String PROFILE_PREFIX = "application";
	private static final String PROFILE_SUFFIX = ".yml";
	private static final String DEFAULT_PROFILE = "application-dev.yml";
	private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

	static {
		try {
			metaYmlData = new Yaml().loadAs(new ClassPathResource(META_PROFILE).getInputStream(), LinkedHashMap.class);
			Properties properties = System.getProperties();
			String active = properties.getProperty(SPRING_PROFILES_ACTIVE);
			if (isBlank(active)) {
				active = getValueFromData(SPRING_PROFILES_ACTIVE, SPRING_PROFILES_ACTIVE.split("\\."), metaYmlData);
			}
			String configName = isNotBlank(active) ? PROFILE_PREFIX + "-" + active + PROFILE_SUFFIX : DEFAULT_PROFILE;
			ClassPathResource classPathResource = new ClassPathResource(configName);
			profileExist = classPathResource.exists();
			if (profileExist) {
				profileYmlData = new Yaml().loadAs(classPathResource.getInputStream(), LinkedHashMap.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("SpringEnvironmentLookup initialize fail");
		}
	}

	@Override
	public String lookup(LogEvent event, String key) {
		return map.computeIfAbsent(key, SpringEnvironmentLookup::resolveYmlMapByKey);
	}

	private static String resolveYmlMapByKey(String key) {
		Assert.isTrue(isNotBlank(key), "key can not be blank!");
		String[] keyChain = key.split("\\.");
		String value = null;
		if (profileExist) {
			value = getValueFromData(key, keyChain, profileYmlData);
		}
		if (isBlank(value)) {
			value = getValueFromData(key, keyChain, metaYmlData);
		}
		return value;
	}

	private static String getValueFromData(String key, String[] keyChain, LinkedHashMap dataMap) {
		int length = keyChain.length;
		if (length == 1) {
			return getFinalValue(key, dataMap);
		}
		String k;
		LinkedHashMap[] mapChain = new LinkedHashMap[length];
		mapChain[0] = dataMap;
		for (int i = 0; i < length; i++) {
			if (i == length - 1) {
				return getFinalValue(keyChain[i], mapChain[i]);
			}
			k = keyChain[i];
			Object o = mapChain[i].get(k);
			if (Objects.isNull(o)) {
				return "";
			}
			if (o instanceof LinkedHashMap) {
				mapChain[i + 1] = (LinkedHashMap) o;
			} else {
				throw new IllegalArgumentException();
			}
		}
		return "";
	}

	private static String getFinalValue(String k, LinkedHashMap ymlData) {
		return defaultIfNull((String) ymlData.get(k), "");
	}
}