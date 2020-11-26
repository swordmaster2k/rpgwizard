/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.board.Board;
import org.rpgwizard.common.assets.board.Event;
import org.rpgwizard.common.assets.Image;
import org.rpgwizard.common.assets.Item;
import org.rpgwizard.common.assets.Point;
import org.rpgwizard.common.assets.Program;
import org.rpgwizard.common.assets.Trigger;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.common.assets.animation.SpriteSheet;
import org.rpgwizard.common.assets.tileset.Tileset;
import org.rpgwizard.common.assets.board.BoardLayer;
import org.rpgwizard.common.assets.board.BoardLayerImage;
import org.rpgwizard.common.assets.board.BoardSprite;
import org.rpgwizard.common.assets.board.BoardVector;
import org.rpgwizard.common.assets.board.BoardVectorType;
import org.rpgwizard.common.assets.files.FileAssetHandleResolver;
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
        assetManager.registerSerializer(new JsonBoardSerializer());
        assetManager.registerSerializer(new JsonGameSerializer());
        assetManager.registerSerializer(new JsonItemSerializer());
        assetManager.registerSerializer(new JsonSpriteSerializer());
        assetManager.registerSerializer(new TextProgramSerializer());
        assetManager.registerSerializer(new JsonTilesetSerializer());
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
        Assert.assertEquals("attack1_north.png", spriteSheet.getFileName());
        Assert.assertEquals(0, spriteSheet.getX());
        Assert.assertEquals(0, spriteSheet.getY());
        Assert.assertEquals(55, spriteSheet.getWidth());
        Assert.assertEquals(90, spriteSheet.getHeight());

        Assert.assertEquals("hit.wav", asset.getSoundEffect());
    }

    @Test
    public void testBoardSerializier() throws Exception {
        String path = AssetSerializerTestHelper.getPath(
                "Boards/Room.board");
        JsonBoardSerializer serializer = new JsonBoardSerializer();

        // Deserialize original.
        Board asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkBoard(asset);

        // Serialize a temporary version and deserialize it.
        path = AssetSerializerTestHelper.serialize(asset, serializer);
        asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkBoard(asset);
    }

    private void checkBoard(Board asset) {
        Assert.assertEquals("Room", asset.getName());
        Assert.assertEquals("An empty room.", asset.getDescription());
        Assert.assertEquals(3, asset.getWidth());
        Assert.assertEquals(3, asset.getHeight());
        Assert.assertEquals(32, asset.getTileWidth());
        Assert.assertEquals(32, asset.getTileHeight());
        Assert.assertTrue(asset.getTileSets().containsKey("Default.tileset"));

        Assert.assertTrue(asset.getLayers().get(0).getSprites().size() == 1);
        BoardSprite sprite = asset.getLayers().get(0).getSprites().get(0);
        Assert.assertEquals("block1", sprite.getId());
        Assert.assertEquals("Block.npc", sprite.getFileName());
        Assert.assertEquals(1, sprite.getX());
        Assert.assertEquals(2, sprite.getY());
        Assert.assertEquals(0, sprite.getLayer());

        Assert.assertTrue(asset.getLayers().size() == 1);
        BoardLayer layer = asset.getLayers().get(0);
        Assert.assertEquals("Floor", layer.getName());
        Assert.assertEquals(3, layer.getTiles()[0].length);
        Assert.assertEquals(3, layer.getTiles()[1].length);

        Assert.assertTrue(layer.getVectors().size() == 2);
        BoardVector vector = layer.getVectors().get(0);
        Assert.assertEquals("walls", vector.getId());
        Assert.assertEquals(2, vector.getPoints().size());
        Assert.assertEquals(true, vector.isClosed());
        Assert.assertEquals(BoardVectorType.SOLID, vector.getType());
        vector = layer.getVectors().get(1);
        Assert.assertEquals("trigger", vector.getId());
        Assert.assertEquals(2, vector.getPoints().size());
        Assert.assertEquals(true, vector.isClosed());
        Assert.assertEquals(BoardVectorType.PASSABLE, vector.getType());
        
        Assert.assertTrue(layer.getImages().size() == 2);
        BoardLayerImage image = layer.getImages().get(0);
        Assert.assertEquals("image1", image.getId());
        Assert.assertEquals(40, image.getX());
        Assert.assertEquals(40, image.getY());
        Assert.assertEquals("1.png", image.getSrc());
        
        image = layer.getImages().get(1);
        Assert.assertEquals("image2", image.getId());
        Assert.assertEquals(80, image.getX());
        Assert.assertEquals(80, image.getY());
        Assert.assertEquals("2.png", image.getSrc());

        Assert.assertEquals(1, asset.getStartingPositionX());
        Assert.assertEquals(3, asset.getStartingPositionY());
        Assert.assertEquals(0, asset.getStartingLayer());

        Assert.assertEquals("room.prg", asset.getFirstRunProgram());
        Assert.assertEquals("room.wav", asset.getBackgroundMusic());
    }

    @Test
    public void testTileSetSerializer() throws Exception {
        String path = AssetSerializerTestHelper.getPath(
                "TileSets/Default.tileset");
        JsonTilesetSerializer serializer = new JsonTilesetSerializer();

        // Deserialize original.
        Tileset asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkTileSet(asset);

        // Serialize a temporary version and deserialize it.
        path = AssetSerializerTestHelper.serialize(asset, serializer);
        asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkTileSet(asset);
    }

    private void checkTileSet(Tileset asset) {
        Assert.assertEquals("Default.tileset", asset.getName());
        Assert.assertEquals(24, asset.getTileWidth());
        Assert.assertEquals(24, asset.getTileHeight());
        Assert.assertEquals("tiles/oryx_16bit_scifi_world_trans.png", asset.getImage());
        
        Map<String, Map<String, String>> tileData = new HashMap<>();
        tileData.put("44", Map.of("defence", "1", "custom", "", "type", "plain"));
        tileData.put("36", Map.of("defence", "4", "custom", "", "type", "mountain"));
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
        Map<String, String> animations = Map.of(
                "north", "north.animation",
                "south", "south.animation",
                "east", "east.animation",
                "west", "west.animation"
        );
        // @formatter:off
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
        // @formatter:off
        Assert.assertEquals(collider, asset.getCollider());
        
        Trigger trigger = new Trigger();
        trigger.setEnabled(true);
        trigger.setX(-20);
        trigger.setY(-5);
        trigger.setEvents(List.of(new org.rpgwizard.common.assets.Event("keypress", "my-event.js", "E")));
        // @formatter:off
        trigger.setPoints(List.of(
                new Point(0, 0),
                new Point(40, 0),
                new Point(40, 30),
                new Point(0, 30)
        ));
        // @formatter:off
        Assert.assertEquals(trigger, asset.getTrigger());
        
        // @formatter:off
        Map<String, String> data = Map.of(
                "key-1", "value-1",
                "key-2", "value-2"
        );
        // @formatter:off
        Assert.assertEquals(data, asset.getData());
        
        Assert.assertEquals("2.0.0", asset.getVersion());
    }

    @Test
    public void testProgramSerializer() throws Exception {
        String path = AssetSerializerTestHelper.getPath(
                "Programs/Startup.js");
        TextProgramSerializer serializer = new TextProgramSerializer();

        // Deserialize original.
        Program asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkProgram(asset);
    }

    @Test
    public void testItemSerializier() throws Exception {
        String path = AssetSerializerTestHelper.getPath(
                "Items/sword.item");
        JsonItemSerializer serializer = new JsonItemSerializer();

        // Deserialize original.
        Item asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkItem(asset);

        // Serialize a temporary version and deserialize it.
        path = AssetSerializerTestHelper.serialize(asset, serializer);
        asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        checkItem(asset);
    }
    
    @Test
    public void testImageSerializier() throws Exception {
        String path = AssetSerializerTestHelper.getPath("Graphics/Idle_north.png");
        ImageSerializer serializer = new ImageSerializer();

        // Deserialize original.
        Image asset = AssetSerializerTestHelper.deserializeFile(path, serializer);
        Assert.assertNotNull(asset.getBufferedImage());
        Assert.assertEquals(55, asset.getBufferedImage().getWidth());
        Assert.assertEquals(90, asset.getBufferedImage().getHeight());
    }

    private void checkItem(Item asset) {
        Assert.assertEquals("Sword", asset.getName());
        Assert.assertEquals("Sword/sword_icon.png", asset.getIcon());
        Assert.assertEquals("The sword of evil's bane.", asset.getDescription());
        Assert.assertEquals("sword", asset.getType());
        Assert.assertEquals(100, asset.getPrice());
        Assert.assertEquals(0.0, asset.getHealthEffect(), 0.0);
        Assert.assertEquals(100.0, asset.getAttackEffect(), 0.0);
        Assert.assertEquals(0.0, asset.getDefenceEffect(), 0.0);
        Assert.assertEquals(0.0, asset.getMagicEffect(), 0.0);
    }

    private void checkProgram(Program asset) throws IOException {
        String code = FileUtils.readFileToString(asset.getFile(), "UTF-8");
        Assert.assertEquals(code, asset.getProgramBuffer().toString());
    }

    private void checkMapsEqual(Map<String, String> expected, Map<String, String> actual) {
        Assert.assertEquals(expected.keySet(), actual.keySet());
        Assert.assertArrayEquals(expected.values().toArray(), actual.values().toArray());
    }

    private BoardVector buildBoardVector(BoardVectorType type, boolean isClosed, String handle, int layer, ArrayList<java.awt.Point> points, ArrayList<Event> events) {
        BoardVector boardVector = new BoardVector();
        boardVector.setType(type);
        boardVector.setIsClosed(isClosed);
        boardVector.setId(handle);
        boardVector.setLayer(layer);
        boardVector.setPoints(points);
        boardVector.setEvents(events);

        return boardVector;
    }

}
