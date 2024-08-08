// Trigger exmaple requets on console with
// curl -v  -X POST http://localhost:8080/send-request\?serviceKey\=example-service\&sandboxKey\=review
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/tunnel'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/services/example-service/sandboxes/review', (request) => {
        showRequest(request.body);
        setIdentifier(JSON.parse(request.body).id)
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}


function setIdentifier(id) {
    $('#identifier').val(id);
}

function sendName() {
    let body = JSON.stringify({'statusCode': Number.parseInt($('#status').val()), 'body': `\`${$('#body').val()}\``});
    let identifier = $('#identifier').val();
    stompClient.publish({
        destination: "/app/tunnel/requests/" + identifier,
        body: body
    });
    let selector = `#${identifier} > [data-response]`;
    console.log('Setting response with ' + selector);
    $(selector).text(body);
}

function showRequest(message) {
    $("#greetings").append(`<tr id='${JSON.parse(message).id}'><td>${message}</td><td data-response></td></tr>`);
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#send").click(() => sendName());
});
