package net.n2oapp.framework.config.persister.control;

import net.n2oapp.framework.api.metadata.control.list.N2oInputSelect;
import net.n2oapp.framework.config.persister.util.PersisterJdomUtil;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.stereotype.Component;

/**
 * @author V. Alexeev.
 */
@Component
public class N2oInputSelectPersister extends N2oControlXmlPersister<N2oInputSelect> {

    @Override
    public Element persist(N2oInputSelect entity, Namespace namespace) {
        Element element = new Element(getElementName(), namespacePrefix, namespaceUri);
        setControl(element, entity);
        setField(element, entity);
        setListField(element, entity);
        setListQuery(element, entity);
        setOptionsList(element, entity.getOptions());
        PersisterJdomUtil.setAttribute(element, "type", entity.getType());
        PersisterJdomUtil.setAttribute(element, "store-on-input", entity.getResetOnBlur() != null ? !entity.getResetOnBlur() : null);
        return element;
    }

    @Override
    public Class<N2oInputSelect> getElementClass() {
        return N2oInputSelect.class;
    }

    @Override
    public String getElementName() {
        return "input-select";
    }
}
