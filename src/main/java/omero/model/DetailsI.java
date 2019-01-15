/*
 * Copyright (C) 2006-2013 University of Dundee & Open Microscopy Environment.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package omero.model;

import java.util.Map;

import Ice.Object;
import ome.system.EventContext;


// Originally generated by templates/java_objects.vm

public class DetailsI extends Details implements ome.model.ModelBased {

    public static final Ice.ObjectFactory makeFactory(final omero.client client) {

        return new Ice.ObjectFactory() {

            public Object create(String arg0) {
                return new DetailsI(client);
            }

            public void destroy() {
                // no-op
            }

        };
    };

    public final static Ice.ObjectFactory Factory = makeFactory(null);

    protected final omero.client client;

    protected final omero.api.ServiceFactoryPrx session;

    public DetailsI() {
        this(null);
    }

    public DetailsI(omero.client client) {
        this.client = client;
        if (client != null) {
            this.session = client.getSession();
        } else {
            this.session = null;
        }
    }

    public omero.client getClient() {
        return this.client;
    }

    public omero.api.ServiceFactoryPrx getSession() {
        return this.session;
    }

    public omero.sys.EventContext getEventContext() {
        return this.event;
    }

    public Map<String, String> getCallContext() {
        return this.call;
    }

    public omero.model.Experimenter getOwner(Ice.Current current) {
        return this.owner;
    }

    public void setOwner(omero.model.Experimenter owner, Ice.Current current) {
        this.owner = owner;

    }

    public omero.model.ExperimenterGroup getGroup(Ice.Current current) {
        return this.group;
    }

    public void setGroup(omero.model.ExperimenterGroup group,
            Ice.Current current) {
        this.group = group;

    }

    public omero.model.Event getCreationEvent(Ice.Current current) {
        return this.creationEvent;
    }

    public void setCreationEvent(omero.model.Event creationEvent,
            Ice.Current current) {
        this.creationEvent = creationEvent;

    }

    public omero.model.Event getUpdateEvent(Ice.Current current) {
        return this.updateEvent;
    }

    public void setUpdateEvent(omero.model.Event updateEvent,
            Ice.Current current) {
        this.updateEvent = updateEvent;

    }

    public omero.model.Permissions getPermissions(Ice.Current current) {
        return this.permissions;
    }

    public void setPermissions(omero.model.Permissions permissions,
            Ice.Current current) {
        this.permissions = permissions;

    }

    public omero.model.ExternalInfo getExternalInfo(Ice.Current current) {
        return this.externalInfo;
    }

    public void setExternalInfo(omero.model.ExternalInfo externalInfo,
            Ice.Current current) {
        this.externalInfo = externalInfo;

    }

    private void ellideEventUuid(Event evt) {
        if (evt != null && evt.session != null) {
            evt.session.uuid = omero.rtypes.rstring("********");
        }

    }

    @SuppressWarnings("unchecked")
    public void copyObject(ome.util.Filterable model,
            ome.util.ModelMapper _mapper) {
        omero.util.IceMapper mapper = (omero.util.IceMapper) _mapper;
        if (model instanceof ome.model.internal.Details) {
            ome.model.internal.Details source = (ome.model.internal.Details) model;
            this.setOwner((omero.model.ExperimenterI) mapper.findTarget(source
                    .getOwner()));
            this.setGroup((omero.model.ExperimenterGroupI) mapper
                    .findTarget(source.getGroup()));
            this.setCreationEvent((omero.model.EventI) mapper.findTarget(source
                    .getCreationEvent()));
            this.setUpdateEvent((omero.model.EventI) mapper.findTarget(source
                    .getUpdateEvent()));

            ellideEventUuid(creationEvent);
            ellideEventUuid(updateEvent);

            this.setExternalInfo((omero.model.ExternalInfoI) mapper
                    .findTarget(source.getExternalInfo()));

            // Since ome.system.EventContext is later in the build
            // graph than ome.model.internal.Details, it's only
            // possible to load it as a java.lang.Object.
            // Note: call context will frequently be null.
            this.event = omero.util.IceMapper.convert(
                    (EventContext) source.contextAt(0));
            if (this.event != null) {
                this.event.sessionUuid = "*********";
            }
            this.call = (Map<String, String>) source.contextAt(1);

            ome.model.internal.Permissions sourceP = source.getPermissions();
            if (sourceP != null) {
                this.setPermissions(new PermissionsI(sourceP));
            }
        } else {
            throw new IllegalArgumentException("Details cannot copy from "
                    + (model == null ? "null" : model.getClass().getName()));
        }
    }

    public ome.util.Filterable fillObject(ome.util.ReverseModelMapper _mapper) {
        omero.util.IceMapper mapper = (omero.util.IceMapper) _mapper;
        ome.model.internal.Details target = ome.model.internal.Details.create(null);
        mapper.store(this, target);
        target.setOwner(
                (ome.model.meta.Experimenter) mapper
                        .reverse((ome.model.ModelBased) this.getOwner()));
        target.setGroup(
                (ome.model.meta.ExperimenterGroup) mapper
                        .reverse((ome.model.ModelBased) this.getGroup()));
        target.setCreationEvent(
                        (ome.model.meta.Event) mapper
                                .reverse((ome.model.ModelBased) this
                                        .getCreationEvent()));
        target.setUpdateEvent(
                (ome.model.meta.Event) mapper
                        .reverse((ome.model.ModelBased) this.getUpdateEvent()));
        target.setExternalInfo(
                        (ome.model.meta.ExternalInfo) mapper
                                .reverse((ome.model.ModelBased) this
                                        .getExternalInfo()));

        Permissions sourceP = this.getPermissions();
        ome.model.internal.Permissions targetP = null;
        if (sourceP != null) {
                targetP = ome.util.Utils.toPermissions(sourceP.getPerm1());
                targetP = new ome.model.internal.Permissions(targetP);
        }
        target.putAt(ome.model.internal.Details.PERMISSIONS, targetP);

        return target;
    }

    public void unload(Ice.Current c) {
        this.setOwner(null);
        this.setGroup(null);
        this.setCreationEvent(null);
        this.setUpdateEvent(null);
        this.setPermissions(null);
        this.setExternalInfo(null);
    }

}
