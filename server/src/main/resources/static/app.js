// Trigger exmaple requets on console with
// curl -v  -X POST http://localhost:8080/send-request\?serviceKey\=example-service\&sandboxKey\=review
const stompClient = new StompJs.Client({
    brokerURL: '/tunnel'
});

function headers(xhr) {
    const headers = xhr.getAllResponseHeaders();

    // Convert the header string into an array
    // of individual headers
    const arr = headers.trim().split(/[\r\n]+/);

    // Create a map of header names to values
    const headerMap = {};
    arr.forEach((line) => {
        const parts = line.split(": ");
        const header = parts.shift();
        const value = parts.join(": ");
        headerMap[header] = value;
    });

    return headerMap
}

function forwardRequest(request) {
    // TODO make request from browser to localhost that's set up.
    let host = $('#host').val();
    console.log('HOST: ' + host);
//     data class Request(val id: UUID, val serviceKey: String, val sandboxKey: String, val path: String, val method: String, val body: String,)
    $.ajax(host + request.path, {
        method: request.method,
        success: function (data, textStatus, xhr) {
            console.log(data);
            console.log(textStatus);
            console.log(xhr.status);
            let body = JSON.stringify({statusCode: xhr.status, body: `"${JSON.stringify(data)}"`, headers: headers(xhr)});
            stompClient.publish({
                destination: "/app/tunnel/requests/" + request.id,
                body: body
            });
            let selector = `#${request.id} > [data-response]`;
            console.log('Setting response with ' + selector);
            $(selector).text(body);
        },
        complete: function (data, textStatus, xhr) {
            console.log(xhr.status)
        }
    })
}

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", !connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
        $("#subscription").show();
    } else {
        $("#subscription").hide();
    }
    $("#greetings").html("");
}

function connect() {
    console.log('Connecting!')
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function subscribe() {
    let serviceKey = $('#serviceKey').val();
    let sandboxKey = $('#sandboxKey').val();
    stompClient.subscribe(`/topic/services/${serviceKey}/sandboxes/${sandboxKey}`, (request) => {
        showRequest(request.body);
        forwardRequest(JSON.parse(request.body));
    });
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
    $("#connect")
        .on('submit', (e) => e.preventDefault())
        .click(() => subscribe());
    $("#disconnect").click(() => disconnect());

    $( document ).ready(function() {
       connect();
    });
});
