package org.iana.rzm.web.tapestry.components.nameserver;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.iana.commons.DateUtil;
import org.iana.rzm.web.common.model.NameServerValue;

import java.util.*;

public abstract class ListNameServers extends BaseComponent {

    @Component(id = "list", type = "For", bindings = {
        "element=literal:tr", "source=prop:sortedNameServers", "value=prop:nameServer"})
    public abstract IComponent getListComponent();

    @Component(id = "hostname", type = "Insert", bindings = {"value=prop:nameServer.hostName", "class=prop:style"})
    public abstract IComponent getHostnameComponent();

    @Component(id = "ipList", type = "Insert", bindings = {"value=prop:nameServer.ips", "class=prop:style"})
    public abstract IComponent getIplistComponent();

    @Component(id = "editible", type = "If", bindings = {"condition=prop:editible"})
    public abstract IComponent getEditibleComponent();

    @Component(id = "shared", type = "If", bindings = {"condition=prop:nameServer.shared"})
    public abstract IComponent getSharedComponent();

    @Component(id = "sharedMessage", type = "If", bindings = {"condition=prop:displaySharedMessage"})
    public abstract IComponent getSharedMessageComponent();


    @Component(id = "edit", type = "DirectLink", bindings = {"listener=prop:listener",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getEditComponent();

    @Component(id = "lastUpdated", type = "Insert", bindings = {"value=prop:lastUpdated"})
    public abstract IComponent getlastUpdatedComponent();

    @Asset("orange_dot.png")
    public abstract IAsset getOrangeDotImage();

    @Asset("grey_dot.png")
    public abstract IAsset getGrayDotImage();

    public abstract List<NameServerValue> getNameServers();
    public abstract long getDomainId();
    public abstract boolean isEditible();
    public abstract IActionListener getListener();
    public abstract NameServerValue getNameServer();

    public List<NameServerValue> getSortedNameServers() {
        List<NameServerValue> list = new ArrayList<NameServerValue>(getNameServers());
        Collections.sort(list, new Comparator<NameServerValue>() {
            public int compare(NameServerValue o1, NameServerValue o2) {
                return o1.getHostName().compareTo(o2.getHostName());
            }
        });

        return list;
    }

    public String getIpList() {
        return getNameServer().getIps();
    }

    public String getStyle() {
        NameServerValue nameServer = getNameServer();
        if (nameServer.isNewOrModified()) {
            return "edited";
        } else if (nameServer.isDelete()) {
            return "strikethrough";
        }
        return null;
    }

    public IAsset getImage() {
        NameServerValue nameServer = getNameServer();
        if (nameServer.isNewOrModified()) {
            return getOrangeDotImage();
        }

        return getGrayDotImage();
    }

    public IAsset getMessageImage() {

        List<NameServerValue> list = getNameServers();
        if (list == null || list.size() == 0) {
            return getGrayDotImage();
        }
        return getGrayDotImage();

    }

    public boolean isDisplaySharedMessage() {
        List<NameServerValue> list = getNameServers();
        if (list == null || list.size() == 0) {
            return false;
        }

        for (NameServerValue nameServerValue : list) {
            if (nameServerValue.isShared()) {
                return true;
            }
        }

        return false;
    }

    public String getLastUpdated() {
        List<NameServerValue> list = getNameServers();
        if (list.size() == 0) {
            return "";
        }

        Date d = list.get(0).getLastUpdated();
        for (NameServerValue nameServerValue : list) {
            Date lastUpdated = nameServerValue.getLastUpdated();
            if (lastUpdated == null) {
                lastUpdated = new Date(System.currentTimeMillis());
            }
            if (d == null || !d.after(lastUpdated)) {
                d = nameServerValue.getLastUpdated();
            }
        }

        return d == null ? "" : "Last updated " + DateUtil.formatDate(d);
    }

}
