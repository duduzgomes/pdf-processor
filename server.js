const http = require("http");
const crypto = require("crypto");

const WEBHOOK_SECRET = "J2u3za1RgFukE4G36ZYvsXW6a+Wc1g5xT4bZpxN8BIE=";
const TIMESTAMP_TOLERANCE_SECONDS = 5 * 60;

function verifyWebhook(webhookId, timestamp, signature, body) {
  const now = Math.floor(Date.now() / 1000);
  const diff = Math.abs(now - parseInt(timestamp));

  if (diff > TIMESTAMP_TOLERANCE_SECONDS) {
    console.error("Webhook rejeitado — timestamp fora da janela de tolerância");
    console.error(
      `Diferença: ${diff}s — máximo permitido: ${TIMESTAMP_TOLERANCE_SECONDS}s`,
    );
    return false;
  }

  const signedContent = `${webhookId}.${timestamp}.${body}`;

  const secretBytes = Buffer.from(WEBHOOK_SECRET, "base64");
  const expectedSignature = crypto
    .createHmac("sha256", secretBytes)
    .update(signedContent, "utf8")
    .digest("base64");

  const parts = signature.split(",");
  if (parts.length !== 2 || parts[0] !== "v1") {
    console.error("Webhook rejeitado — formato de assinatura inválido");
    return false;
  }
  const receivedSignature = parts[1];

  const expectedBuffer = Buffer.from(expectedSignature);
  const receivedBuffer = Buffer.from(receivedSignature);

  if (expectedBuffer.length !== receivedBuffer.length) {
    console.error("Webhook rejeitado — tamanho da assinatura inválido");
    return false;
  }

  const valid = crypto.timingSafeEqual(expectedBuffer, receivedBuffer);

  if (!valid) {
    console.error("Webhook rejeitado — assinatura inválida");
    console.error("Esperada: ", expectedSignature);
    console.error("Recebida: ", receivedSignature);
  }

  return valid;
}

const server = http.createServer((req, res) => {
  if (req.method === "POST") {
    let body = "";
    req.on("data", (chunk) => (body += chunk));
    req.on("end", () => {
      const webhookId = req.headers["webhook-id"];
      const webhookTimestamp = req.headers["webhook-timestamp"];
      const webhookSignature = req.headers["webhook-signature"];

      console.log("Headers:");
      console.log("  webhook-id:        ", webhookId);
      console.log("  webhook-timestamp: ", webhookTimestamp);
      console.log("  webhook-signature: ", webhookSignature);
      console.log("Body:", JSON.parse(body));

      // Verifica a assinatura
      const valid = verifyWebhook(
        webhookId,
        webhookTimestamp,
        webhookSignature,
        body,
      );

      if (valid) {
        console.log("✅ Assinatura válida — webhook autêntico");
        res.writeHead(200);
        res.end("OK");
      } else {
        console.log("❌ Assinatura inválida — webhook rejeitado");
        res.writeHead(401);
        res.end("Unauthorized");
      }
    });
  }
});

server.listen(3000, () => {
  console.log("Servidor rodando na porta 3000");
});
