package top.yuuna.springfamework.beans.factory.support;

import top.yuuna.springfamework.beans.BeansException;
import top.yuuna.springfamework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;

/**
 * 这里只做Bean的实例化操作
 * @author NGinko
 * @date 2022-01-25 21:13
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    /**
     * 这里默认选择JDK的作为实现
     */
    private InstantiationStrategy cglibInstantiationStrategy = new CglibSubclassingInstantiationStrategy();
    private InstantiationStrategy simpleInstantiationStrategy = new SimpleInstantiationStrategy();

    private String methodInstance = InstantiationMethod.JdkDefault.name();

    public AbstractAutowireCapableBeanFactory() {
    }

    /**
     *
     * @param methodInstance 实例化的方式选择
     */
    public AbstractAutowireCapableBeanFactory(String methodInstance) {
        this.methodInstance = methodInstance;
    }

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;
        try {
            bean = createBeanInstance(beanDefinition, beanName, args);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }
        addSingleton(beanName, bean);
        return bean;
    }


    /**
     * 获取构造函数
     * 匹配入参args和获得的构造韩式集合的匹配情况
     * @param beanDefinition
     * @param beanBeanName
     * @param args
     * @return
     */
    protected Object createBeanInstance(BeanDefinition beanDefinition, String beanBeanName, Object[] args) {
        Constructor constructorToUse = null;
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        for (Constructor ctor : declaredConstructors) {
            //TODO 先做简单的参数长度判断，后面还需要做类型比对
            if (null != ctor && ctor.getParameterTypes().length == args.length) {
                constructorToUse = ctor;
                break;
            }
        }
        InstantiationStrategy instantiationStrategy ;
        if (methodInstance.equals(InstantiationMethod.Cglib.name())) {
            instantiationStrategy = getCglibInstantiationStrategy();
        } else {
            instantiationStrategy = getSimpleInstantiationStrategy();
        }
        return instantiationStrategy.instantiation(beanDefinition, beanBeanName, constructorToUse, args);
    }

    public InstantiationStrategy getSimpleInstantiationStrategy() {
        return simpleInstantiationStrategy;
    }


    public InstantiationStrategy getCglibInstantiationStrategy() {
        return cglibInstantiationStrategy;
    }

}

enum InstantiationMethod {
    //实例化的方式选择
    Cglib,
    JdkDefault;
}
