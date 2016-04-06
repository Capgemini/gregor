package com.capgemini.gregor.internal.producer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;

public class KafkaProducerBeanDefinitionFactoryTest {

    private KafkaProducerBeanDefinitionFactory underTest = KafkaProducerBeanDefinitionFactory.getInstance();
    
    @Test
    public void testCreate() {
        final List<ProducerDetails> details = createProducerDetails("topic1", "topic2");
        final Set<BeanDefinitionHolder> definitions = underTest.create(details);
    
        assertDefinitionsAreCorrect(definitions, details);
    }
    
    private void assertDefinitionsAreCorrect(Set<BeanDefinitionHolder> definitions, List<ProducerDetails> expectedDetails) {
        
        assertEquals("Too many bean definitions created", 1, definitions.size());
        
        final BeanDefinitionHolder holder = definitions.iterator().next();

        assertEquals("Incorrect class type in created bean definition", 
                GregorClientInstanceFactoryBean.class.getName(), holder.getBeanDefinition().getBeanClassName());
        
        
        final ValueHolder argValue = holder.getBeanDefinition().getConstructorArgumentValues().getArgumentValue(0, List.class);
        
        assertEquals("Incorrect producer details set on factory", expectedDetails, argValue.getValue());
    }
    
    private List<ProducerDetails> createProducerDetails(String... topics) {
        
        final List<ProducerDetails> details = new ArrayList<ProducerDetails>();
        for (String topic : topics) {
            final MutableProducerDetails detail = new MutableProducerDetails();
            detail.setTopicName(topic);
            
            details.add(detail);
        }
        
        return details;
    }
}
