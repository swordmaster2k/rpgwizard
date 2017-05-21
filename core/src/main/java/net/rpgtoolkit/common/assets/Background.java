/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets;

public class Background extends AbstractAsset {

  private AssetDescriptor backgroundImage;
  private AssetDescriptor backgroundMusic;
  private AssetDescriptor selectingSound;
  private AssetDescriptor selectionSound;
  private AssetDescriptor readySound;
  private AssetDescriptor invalidSelectionSound;

  public Background(AssetDescriptor descriptor) {
    super(descriptor);
  }

  public AssetDescriptor getBackgroundImage() {
    return this.backgroundImage;
  }

  public void setBackgroundImage(AssetDescriptor descriptor) {
    this.backgroundImage = descriptor;
  }

  public AssetDescriptor getBackgroundMusic() {
    return this.backgroundMusic;
  }

  public void setBackgroundMusic(AssetDescriptor descriptor) {
    this.backgroundMusic = descriptor;
  }

  public AssetDescriptor getSelectingSound() {
    return this.selectingSound;
  }

  public void setSelectingSound(AssetDescriptor descriptor) {
    this.selectingSound = descriptor;
  }

  public AssetDescriptor getSelectionSound() {
    return this.selectionSound;
  }

  public void setSelectionSound(AssetDescriptor descriptor) {
    this.selectionSound = descriptor;
  }

  public AssetDescriptor getReadySound() {
    return this.readySound;
  }

  public void setReadySound(AssetDescriptor descriptor) {
    this.readySound = descriptor;
  }

  public AssetDescriptor getInvalidSelectionSound() {
    return this.invalidSelectionSound;
  }

  public void setInvalidSelectionSound(AssetDescriptor descriptor) {
    this.invalidSelectionSound = descriptor;
  }

}
