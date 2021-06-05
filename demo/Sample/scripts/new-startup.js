export default async function (origin) {
    rpg.setColor(255, 0, 0, 1);
    rpg.setAlpha(0.5);

    rpg.drawCircle("default", 100, 100, 50);
    rpg.fillCircle("default", 205, 100, 50);

    rpg.setAlpha(1);

    rpg.createCanvas("test", 500, 500);

    rpg.drawRect("test", 100, 205, 50, 50);
    rpg.fillRect("test", 205, 205, 50, 50);
    rpg.drawRoundedRect("test", 305, 205, 50, 50, 1, 5);

    rpg.drawLine("test", 10, 10, 50, 10, 3);

    rpg.drawText("test", 35, 35, "Hello world!");

    rpg.render("default");
    rpg.render("test");
}
