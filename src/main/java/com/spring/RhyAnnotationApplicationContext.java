package com.spring;

import com.rhy.service.UserService;
import com.spring.annotation.Autowired;
import com.spring.annotation.Component;
import com.spring.annotation.ComponentScan;
import com.spring.annotation.Scope;

import java.beans.Introspector;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Herion Lemon
 * @date: 2021/10/17 20:11
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */
public class RhyAnnotationApplicationContext {
    private Class configClazz;
    private Map<String,BeanDefinition> beanDefinitionMap = new HashMap<>();
    private Map<String,String> aliasMap = new HashMap<>();
    private Map<String,Object> singletonObjects = new HashMap<>();
    public RhyAnnotationApplicationContext(Class configClazz) {
        this.configClazz = configClazz;
        scan();
        /**
         * 实例化剩余(非懒加载Bean)的单例Bean
         */
        finishBeanFactoryInitialization();
    }

    /**
     * 实例化剩余(非懒加载Bean)的单例Bean
     */
    private void finishBeanFactoryInitialization() {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if("singleton".equals(beanDefinition.scope)){
                Object obj = createBean(beanName,beanDefinition.clazz);
                singletonObjects.put(beanName,obj);
            }
        }
    }

    /**
     * 创建对象及后续流程
     * @param beanName
     * @param clazz
     * @return
     */
    private Object createBean(String beanName, Class clazz) {
        //创建对象
        Object instance = doCreateBean(beanName,clazz);
        //填充属性
        populateBean(instance);
        //初始化
        initializeBean(instance);
        return instance;
    }



    /**
     * 实例化对象，选择构造
     * @param beanName
     * @param clazz
     * @return
     */
    private Object doCreateBean(String beanName, Class clazz) {
        Object instance = null;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return instance;
    }

    /**
     * 填充属性
     * @param instance
     */
    private void populateBean(Object instance) {
        Field[] declaredFields = instance.getClass().getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            Field declaredField = declaredFields[i];
            declaredField.setAccessible(true);
            /** 检查是否存在{@link Autowired}注解 **/
            if(declaredField.isAnnotationPresent(Autowired.class)){
                //填充属性
                String beanName = declaredField.getAnnotation(Autowired.class).value();
                if("".equals(beanName)){
                    beanName = declaredField.getName();
                }
                try {
                    declaredField.set(instance,getBean(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化Bean
     * @param instance
     */
    private void initializeBean(Object instance) {
    }
    /**
     * 通过配置类获取扫描包路径，执行扫描包，加载类为{@link BeanDefinition}到{@link this#beanDefinitionMap}中
     */
    private void scan() {
        if(configClazz.isAnnotationPresent(ComponentScan.class)){
            /** 包资源路径 **/
            ComponentScan componentScan = (ComponentScan) configClazz.getAnnotation(ComponentScan.class);
            String packageStr = componentScan.value();
            String path = packageStr.replace(".", "/");
            /** 获取target路径 **/
            ClassLoader classLoader = this.getClass().getClassLoader();
            URL resource = classLoader.getResource(path);
            doScan(resource.getPath(),path);
        }
    }

    /**
     * 执行扫描包路径，加载类为{@link BeanDefinition}到{@link this#beanDefinitionMap}中
     * @param path
     */
    private void doScan(String path,String packagePath) {
        packagePath = packagePath.replace("/","\\");
        /** 读取target目录下资源文件 **/
        File file = new File(path);
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File fileChild = files[i];
                if(fileChild.isFile()){
                    String classPath = fileChild.getPath();
                    Class beanClass = null;
                    try {
                        classPath = classPath.substring(classPath.indexOf(packagePath),classPath.indexOf(".class")).replace("\\",".");
                        beanClass = Class.forName(classPath);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    registerBeanDefinition(beanClass);
                }
                doScan(fileChild.getPath(),packagePath);
            }
        }else{

        }
    }

    /**
     * 注册{@link BeanDefinition}到{@link this#beanDefinitionMap}中
     * @param beanClass
     */
    private void registerBeanDefinition(Class<?> beanClass) {
        if(!beanClass.isInterface()){
            if(beanClass.isAnnotationPresent(Component.class)){
                Component component = beanClass.getAnnotation(Component.class);
                Scope scope = beanClass.getAnnotation(Scope.class);
                String scopeStr = "singleton";
                if(scope != null){
                    scopeStr = scope.value();
                }
                //生成BeanName
                String beanName = getBeanName(component,beanClass);
                BeanDefinition beanDefinition = new BeanDefinition(
                        beanName,
                        scopeStr,
                        beanClass
                );
                beanDefinitionMap.put(beanName,beanDefinition);
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    Class<?> anInterface = interfaces[i];
                    aliasMap.put(getBeanName(component,anInterface),beanName);
                }
            }
        }
    }

    /**
     * 生成Bean默认名字
     * @param component
     * @param beanClass
     * @return
     */
    private String getBeanName(Component component, Class<?> beanClass) {
        String beanName = component.value();
        if("".equals(beanName)){
            beanName = Introspector.decapitalize(beanClass.getSimpleName());
        }
        return beanName;
    }

    public Object getBean(String beanName) throws IllegalAccessException {
        String aliasName = aliasMap.get(beanName);
        if(aliasName != null && aliasName != ""){
            beanName = aliasName;
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if(beanDefinition == null){
            throw new IllegalAccessException("Not found BeanDefinition by beanName 【"+beanName+"】");
        }
        if("singleton".equals(beanDefinition.scope)){
            return singletonObjects.get(beanName);
        }else {
            return createBean(beanName,beanDefinition.clazz);
        }
    }
}
