/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.Event;
import org.rpgwizard.common.assets.Image;
import org.rpgwizard.common.assets.Location;
import org.rpgwizard.common.assets.Point;
import org.rpgwizard.common.assets.Script;
import org.rpgwizard.common.assets.Trigger;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.common.assets.animation.SpriteSheet;
import org.rpgwizard.common.assets.tileset.Tileset;
import org.rpgwizard.common.assets.files.FileAssetHandleResolver;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.map.MapImage;
import org.rpgwizard.common.assets.map.MapLayer;
import org.rpgwizard.common.assets.map.MapSprite;
import org.rpgwizard.common.assets.sprite.Sprite;

/**
 * Some pretty basic checks to ensure the file serializers work.
 *
 * @author Joshua Michael Daly
 */
public class AssetSerializerTest {

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("project.path", "src/test/resources");

        AssetManager assetManager = AssetManager.getInstance();

        assetManager.registerResolver(new FileAssetHandleResolver());

        // Serializers
        assetManager.registerSerializer(new JsonAnimationSerializer());
        assetManager.registerSerializer(new JsonGameSerializer());
        assetManager.registerSerializer(new JsonSpriteSerializer());
        assetManager.registerSerializer(new ScriptSerializer());
        assetManager.registerSerializer(new JsonTileSetSerializer());
        assetManager.registerSerializer(new ImageSerializer());
    }

    @Test
    public void testProjectSerializer() throws Exception {
        String path = AssetSerializerTestHelper.getPath("Test.game");
        JsonGameSerializer serializer = new JsonGameSerializer();

        // Deserialize original.
        Game asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkProject(asset);

        // Serialize a temporary version and deserialize it.
        path = AssetSerializerTestHelper.serialize(asset, serializer);
        asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkProject(asset);
    }

    private void checkProject(Game asset) {
        Assert.assertEquals("The Wizard's Tower", asset.getName());

        Assert.assertNotNull(asset.getViewport());
        Assert.assertEquals(1600, asset.getViewport().getWidth());
        Assert.assertEquals(900, asset.getViewport().getHeight());
        Assert.assertTrue(asset.getViewport().isFullScreen());

        Assert.assertNotNull(asset.getDebug());
        Assert.assertTrue(asset.getDebug().isShowColliders());
        Assert.assertTrue(asset.getDebug().isShowTriggers());
    }

    @Test
    public void testAnimationSerializier() throws Exception {
        String path = AssetSerializerTestHelper.getPath(
                "Animations/Hero_world_attack_north.animation");
        JsonAnimationSerializer serializer = new JsonAnimationSerializer();

        // Deserialize original.
        Animation asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkAnimation(asset);

        // Serialize a temporary version and deserialize it.
        path = AssetSerializerTestHelper.serialize(asset, serializer);
        asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkAnimation(asset);
    }

    private void checkAnimation(Animation asset) {
        Assert.assertEquals(0.2, asset.getFrameRate(), 25);
        Assert.assertEquals(55, asset.getWidth());
        Assert.assertEquals(90, asset.getHeight());

        SpriteSheet spriteSheet = asset.getSpriteSheet();
        Assert.assertEquals("attack1_north.png", spriteSheet.getImage());
        Assert.assertEquals(0, spriteSheet.getX());
        Assert.assertEquals(0, spriteSheet.getY());
        Assert.assertEquals(55, spriteSheet.getWidth());
        Assert.assertEquals(90, spriteSheet.getHeight());

        Assert.assertEquals("hit.wav", asset.getSoundEffect());
    }

    @Test
    public void testMapSerializier() throws Exception {
        String path = AssetSerializerTestHelper.getPath("maps/tower.map");
        JsonMapSerializer serializer = new JsonMapSerializer();

        // Deserialize original.
        Map asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkMap(asset);

        // Serialize a temporary version and deserialize it.
        path = AssetSerializerTestHelper.serialize(asset, serializer);
        asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkMap(asset);
    }

    private void checkMap(Map map) {
        Assert.assertEquals("Tower", map.getName());
        Assert.assertEquals(12, map.getWidth());
        Assert.assertEquals(20, map.getHeight());
        Assert.assertEquals(32, map.getTileWidth());
        Assert.assertEquals(32, map.getTileHeight());
        Assert.assertEquals("Tower.ogg", map.getMusic());
        Assert.assertEquals(List.of("tower.tileset"), map.getTilesets());
        Assert.assertEquals("some-script.js", map.getEntryScript());
        Assert.assertEquals(new Location(191, 558, 0), map.getStartLocation());

        Assert.assertEquals(1, map.getLayers().size());
        MapLayer layer = map.getLayers().get(0);
        Assert.assertEquals("Floor", layer.getId());
        Assert.assertEquals(List.of("-1:-1", "0:1", "0:2"), layer.getTiles());

        Assert.assertEquals(2, layer.getColliders().size());
        Collider collider1 = layer.getColliders().get("collider-1");
        // @formatter:off
        Assert.assertEquals(List.of(
                new Point(64, 352),
                new Point(320, 352)
        ), collider1.getPoints());
        // @formatter:on
        Collider collider2 = layer.getColliders().get("collider-2");
        // @formatter:off
        Assert.assertEquals(List.of(
                new Point(160, 128),
                new Point(160, 160),
                new Point(224, 160),
                new Point(224, 128)
        ), collider2.getPoints());
        // @formatter:on

        Assert.assertEquals(1, layer.getTriggers().size());
        Trigger trigger1 = layer.getTriggers().get("trigger-1");
        Assert.assertEquals(new Event("overlap", "GameOver.js", null), trigger1.getEvent());
        // @formatter:off
        Assert.assertEquals(List.of(
                new Point(160, 128),
                new Point(160, 160),
                new Point(224, 160),
                new Point(224, 128)
        ), trigger1.getPoints());
        // @formatter:on

        Assert.assertEquals(1, layer.getSprites().size());
        MapSprite sprite1 = layer.getSprites().get("sprite-1");
        Assert.assertEquals("Torch.npc", sprite1.getAsset());
        Assert.assertEquals("Idle.js", sprite1.getThread());
        Assert.assertEquals(new Location(112, 80, 0), sprite1.getStartLocation());
        Assert.assertEquals(new Event("keypress", "turn-off.js", null), sprite1.getEvent());

        Assert.assertEquals(1, layer.getImages().size());
        Assert.assertEquals(java.util.Map.of("image-1", new MapImage("rooms/above.png", 120, 12, false, null)),
                layer.getImages());

        Assert.assertEquals("2.0.0", map.getVersion());
    }

    @Test
    public void testTileSetSerializer() throws Exception {
        String path = AssetSerializerTestHelper.getPath(
                "TileSets/Default.tileset");
        JsonTileSetSerializer serializer = new JsonTileSetSerializer();

        // Deserialize original.
        Tileset asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkTileSet(asset);

        // Serialize a temporary version and deserialize it.
        path = AssetSerializerTestHelper.serialize(asset, serializer);
        asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkTileSet(asset);
    }

    private void checkTileSet(Tileset asset) {
        Assert.assertNotNull(asset.getName());
        Assert.assertEquals(24, asset.getTileWidth());
        Assert.assertEquals(24, asset.getTileHeight());
        Assert.assertEquals("tiles/oryx_16bit_scifi_world_trans.png", asset.getImage());

        java.util.Map<String, java.util.Map<String, String>> tileData = new HashMap<>();
        tileData.put("44", java.util.Map.of("defence", "1", "custom", "", "type", "plain"));
        tileData.put("36", java.util.Map.of("defence", "4", "custom", "", "type", "mountain"));
        Assert.assertEquals(tileData, asset.getTileData());
    }

    @Test
    public void testSpriteSerializer() throws Exception {
        String path = AssetSerializerTestHelper.getPath(
                "sprites/hero.sprite");
        JsonSpriteSerializer serializer = new JsonSpriteSerializer();

        // Deserialize original.
        Sprite asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkSpite(asset);

        // Serialize a temporary version and deserialize it.
        path = AssetSerializerTestHelper.serialize(asset, serializer);
        asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkSpite(asset);
    }

    private void checkSpite(Sprite asset) {
        Assert.assertEquals("hero", asset.getName());
        Assert.assertEquals("The hero of time", asset.getDescription());

        // @formatter:off
        java.util.Map<String, String> animations = java.util.Map.of(
                "north", "north.animation",
                "south", "south.animation",
                "east", "east.animation",
                "west", "west.animation"
        );
        // @formatter:on
        Assert.assertEquals(animations, asset.getAnimations());

        Collider collider = new Collider();
        collider.setEnabled(true);
        collider.setX(-15);
        collider.setY(0);
        // @formatter:off
        collider.setPoints(List.of(
                new Point(0, 0),
                new Point(30, 0),
                new Point(30, 20),
                new Point(0, 20)
        ));
        // @formatter:on
        Assert.assertEquals(collider, asset.getCollider());

        Trigger trigger = new Trigger();
        trigger.setEnabled(true);
        trigger.setX(-20);
        trigger.setY(-5);
        trigger.setEvent(new org.rpgwizard.common.assets.Event("keypress", "my-event.js", "E"));
        // @formatter:off
        trigger.setPoints(List.of(
                new Point(0, 0),
                new Point(40, 0),
                new Point(40, 30),
                new Point(0, 30)
        ));
        // @formatter:on
        Assert.assertEquals(trigger, asset.getTrigger());

        // @formatter:off
        java.util.Map<String, String> data = java.util.Map.of(
                "key-1", "value-1",
                "key-2", "value-2"
        );
        // @formatter:on
        Assert.assertEquals(data, asset.getData());

        Assert.assertEquals("2.0.0", asset.getVersion());
    }

    @Test
    public void testScriptSerializer() throws Exception {
        String path = AssetSerializerTestHelper.getPath(
                "scripts/Startup.js");
        ScriptSerializer serializer = new ScriptSerializer();

        // Deserialize original.
        Script asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkScript(asset);
    }

    @Test
    public void testImageSerializier() throws Exception {
        String path = AssetSerializerTestHelper.getPath("textures/Idle_north.png");
        ImageSerializer serializer = new ImageSerializer();

        // Deserialize original.
        Image asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        Assert.assertNotNull(asset.getBufferedImage());
        Assert.assertEquals(55, asset.getBufferedImage().getWidth());
        Assert.assertEquals(90, asset.getBufferedImage().getHeight());
    }

    private void checkScript(Script asset) throws IOException {
        String code = FileUtils.readFileToString(asset.getFile(), "UTF-8");
        Assert.assertEquals(code, asset.getStringBuffer().toString());
    }

}
