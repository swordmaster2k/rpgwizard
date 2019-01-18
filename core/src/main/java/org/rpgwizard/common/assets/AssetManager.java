/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris Hutchinson <chris@cshutchinson.com>
 */
public class AssetManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetManager.class);

    // Singleton.
    // TODO: Singleton pattern is not appropriate for this type.
    private static final AssetManager INSTANCE = new AssetManager();

    private final TreeSet<AssetSerializer> serializers;
    private final List<AssetHandleResolver> resolvers;
    private final Map<AssetDescriptor, AssetHandle> assets;

    public static AssetManager getInstance() {
        return INSTANCE;
    }

    private AssetManager() {
        this.resolvers = new ArrayList<>();
        this.serializers = new TreeSet<>(new AssetSerializer.PriorityComparator());
        this.assets = new HashMap<>();
    }

    /***
     * Returns the number of assets managed.
     * 
     * @return count of assets managed
     */
    public int getAssetCount() {
        return this.assets.size();
    }

    /**
     * Gets the handle for the given asset. If the asset is not already being managed, a handle for it is created and
     * added. If a handle with the asset's descriptor is already being managed, the handle's asset is set to the given
     * asset.
     *
     * @param asset
     * @return a handle for the given asset, or null if the asset is null
     */
    public AssetHandle getHandle(Asset asset) {
        if (asset == null) {
            return null;
        }
        AssetDescriptor d = asset.getDescriptor();
        AssetHandle h;
        if (this.assets.containsKey(d)) {
            h = this.assets.get(d);
        } else {
            h = this.resolve(d);
            this.assets.put(d, h);
        }
        h.setAsset(asset);
        return h;
    }

    /**
     * Removes an asset from the map.
     * 
     * @param asset
     */
    public void removeAsset(Asset asset) {
        if (asset != null && asset.getDescriptor() != null) {
            LOGGER.info("Removing asset in cache with URI=[{}]", asset.getDescriptor().uri);
            assets.remove(asset.getDescriptor());
        }
    }

    /**
     * Registers a serializer with the manager. Serializers are sorted in ascending order of priority, so lower priority
     * numbers are first.
     *
     * @param serializer
     */
    public void registerSerializer(final AssetSerializer serializer) {
        LOGGER.info("Registering serializer=[{}].", serializer);

        if (serializer == null) {
            throw new NullPointerException();
        }
        this.serializers.add(serializer);
    }

    public void registerResolver(final AssetHandleResolver resolver) {
        LOGGER.info("Registering resolver=[{}].", resolver);

        if (resolver == null) {
            throw new NullPointerException();
        }
        this.resolvers.add(resolver);
    }

    public AssetHandle serialize(AssetHandle handle) throws IOException, AssetException {
        final AssetDescriptor descriptor = handle.getDescriptor();
        LOGGER.info("Attempting to serialize asset with URI=[{}]", descriptor.uri);

        if (handle.getAsset() != null) {
            assets.put(descriptor, handle);
        }

        for (AssetSerializer serializer : serializers) {
            if (serializer.serializable(descriptor)) {
                LOGGER.info("Found serializer=[{}] for asset with URI=[{}]", serializer, descriptor.uri);
                serializer.serialize(handle);
                break;
            }
        }

        return handle;
    }

    public AssetHandle deserialize(AssetDescriptor descriptor) throws IOException, AssetException {
        LOGGER.info("Attempting to deserialize asset with URI=[{}]", descriptor.uri);

        if (assets.containsKey(descriptor)) {
            LOGGER.info("Found asset in cache with URI=[{}]", descriptor.uri);
            return assets.get(descriptor);
        }

        final AssetHandle handle = resolve(descriptor);

        if (handle != null) {
            for (AssetSerializer serializer : serializers) {
                if (serializer.deserializable(descriptor)) {
                    LOGGER.info("Found serializer=[{}] for asset with URI=[{}]", serializer, descriptor.uri);
                    serializer.deserialize(handle);
                    if (handle.getAsset() != null) {
                        assets.put(descriptor, handle);
                    }
                    break;
                }
            }
        }

        return handle;

    }

    private AssetHandle resolve(final AssetDescriptor descriptor) {

        for (final AssetHandleResolver resolver : resolvers) {
            if (resolver.resolvable(descriptor)) {
                return resolver.resolve(descriptor);
            }
        }

        return null;

    }

}
