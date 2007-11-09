package org.iana.rzm.web.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.*;

@ComponentClass
public abstract class SortableTableHeader extends BaseComponent {

    @Component(id = "sort", type = "DirectLink", bindings = {"listener=listener:sort", "parameters=prop:sortParameters"})
    public abstract IComponent getSortlinkComponent();

    @Component(id = "header", type = "Insert", bindings = {"value=prop:header"})
    public abstract IComponent getHeaderComponent();

    @Component(id = "header1", type = "Insert", bindings = {"value=prop:header"})
    public abstract IComponent getHeader1Component();

    @Component(id="seeIamge", type = "If", bindings = {"condition=prop:imageVisible"})
    public abstract IComponent getImageVisibleComponent();

    @Component(id="sortImage", type="Image", bindings = {"image=prop:sortImage"})
    public abstract IComponent getImageComponent();

    @Asset("images/sort_arrow_descending.png")
    public abstract IAsset getDescendingArrow();

    @Asset("images/sort_arrow_ascending.png")
    public abstract IAsset getAscendingArrow();

    @Parameter(required = false, defaultValue = "prop:nullSortFactory")
    public abstract SortFactory getSortFactory();

    @Parameter(required = true)
    public abstract boolean isImageVisible();

    @Bean(NullSortFactory.class)
    public abstract SortFactory getNullSortFactory();

    @Persist("client:page")
    public abstract boolean isAccending();
    public abstract void setAccending(boolean value);

    @Parameter(required = true)
    public abstract String getHeader();

    public boolean isSortable() {
        return getSortFactory().isFieldSortable(getHeader());
    }

    public boolean isArrowVisible(String header){
        return getHeader() != null && getHeader().equals(header);
    }
    
    public IAsset getSortImage() {
        if (isAccending()) {
            return getDescendingArrow();
        }
        return getAscendingArrow();
    }

    public Object[] getSortParameters() {
        return new Object[]{getHeader(), isAccending()};
    }

    public void sort(String header, boolean accending) {
        boolean b = !accending;
        setAccending(b);
        getSortFactory().sort(header, accending);
    }
}
