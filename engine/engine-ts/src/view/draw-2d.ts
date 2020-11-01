import { Rgba } from "../client-api/rpg-api";

export namespace Draw2D {

    export function putImageData(ctx: CanvasRenderingContext2D, x: number, y: number, rgba: Rgba) {
        const imageData: ImageData = ctx.getImageData(x, y, 1, 1);
        imageData.data[0] = rgba.r;
        imageData.data[1] = rgba.g;
        imageData.data[2] = rgba.b;
        imageData.data[3] = rgba.a * 255;
        ctx.putImageData(imageData, x, y);
    }

    export function drawCircle(ctx: CanvasRenderingContext2D, x: number, y: number, radius: number) {
        ctx.beginPath();
        ctx.arc(x, y, radius, 0, 2 * Math.PI);
        ctx.stroke();
    }

    export function drawImage(ctx: CanvasRenderingContext2D, image: ImageBitmap, x: number, y: number, width: number, height: number, rotation: number) {
        // rotate around center point
        x += width / 2;
        y += height / 2;
        ctx.translate(x, y);
        ctx.rotate(rotation);
        ctx.drawImage(image, -width / 2, -height / 2, width, height);
        ctx.rotate(-rotation);
        ctx.translate(-x, -y);
    }

    export function drawImagePart(ctx: CanvasRenderingContext2D, image: ImageBitmap, srcX: number, srcY: number, srcWidth: number, srcHeight: number, destX: number, destY: number, destWidth: number, destHeight: number, rotation: number) {
        // rotate around center point
        destX += destWidth / 2;
        destY += destHeight / 2;
        ctx.translate(destX, destY);
        ctx.rotate(rotation);
        ctx.drawImage(image, srcX, srcY, srcWidth, srcHeight, -destWidth / 2, -destHeight / 2, destWidth, destHeight);
        ctx.rotate(-rotation);
        ctx.translate(-destX, -destY);
    }

    export function drawLine(ctx: CanvasRenderingContext2D, x1: number, y1: number, x2: number, y2: number, lineWidth: number) {
        ctx.lineWidth = lineWidth;
        ctx.beginPath();
        ctx.moveTo(x1, y1);
        ctx.lineTo(x2, y2);
        ctx.stroke();
    }

    export function drawRect(ctx: CanvasRenderingContext2D, x: number, y: number, width: number, height: number, lineWidth: number) {
        ctx.lineWidth = lineWidth;
        ctx.strokeRect(x, y, width, height);
    }

    export function drawRoundedRect(ctx: CanvasRenderingContext2D, x: number, y: number, width: number, height: number, lineWidth: number, radius: number) {
        ctx.lineWidth = lineWidth;
        ctx.beginPath();
        ctx.moveTo(x + radius, y);
        ctx.lineTo(x + width - radius, y);
        ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
        ctx.lineTo(x + width, y + height - radius);
        ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
        ctx.lineTo(x + radius, y + height);
        ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
        ctx.lineTo(x, y + radius);
        ctx.quadraticCurveTo(x, y, x + radius, y);
        ctx.closePath();
        ctx.stroke();
    }

    export function drawText(ctx: CanvasRenderingContext2D, x: number, y: number, text: string, font: string) {
        ctx.font = font;
        ctx.fillText(text, x, y);
    }

    export function fillCircle(ctx: CanvasRenderingContext2D, x: number, y: number, radius: number) {
        ctx.beginPath();
        ctx.arc(x, y, radius, 0, 2 * Math.PI);
        ctx.fill();
    }

    export function fillRect(ctx: CanvasRenderingContext2D, x: number, y: number, width: number, height: number) {
        ctx.fillRect(x, y, width, height);
    }

    export function fillRoundedRect(ctx: CanvasRenderingContext2D, x: number, y: number, width: number, height: number, radius: number) {
        ctx.beginPath();
        ctx.moveTo(x + radius, y);
        ctx.lineTo(x + width - radius, y);
        ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
        ctx.lineTo(x + width, y + height - radius);
        ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
        ctx.lineTo(x + radius, y + height);
        ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
        ctx.lineTo(x, y + radius);
        ctx.quadraticCurveTo(x, y, x + radius, y);
        ctx.closePath();
        ctx.fill();
    }

}
