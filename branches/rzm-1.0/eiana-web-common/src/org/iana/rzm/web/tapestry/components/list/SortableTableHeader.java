package org.iana.rzm.web.tapestry.components.list;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;

public abstract class SortableTableHeader extends BaseComponent {

    @Bean(NullSortFactory.class)
    public abstract SortFactory getNullSortFactory();

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

    @Asset("sort_arrow_descending.png")
    public abstract IAsset getDescendingArrow();

    @Asset("sort_arrow_ascending.png")
    public abstract IAsset getAscendingArrow();


    public abstract SortFactory getSortFactory();
    public abstract String getHeader();
    public abstract boolean isImageVisible();

    @Persist("client")
    public abstract boolean isAccending();
    public abstract void setAccending(boolean value);

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
