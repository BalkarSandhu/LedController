function setInputValue(name, value) {
    const input = document.querySelector(`input[name="${name}"]`);
    if (input) {
        input.value = value;
    } else {
        console.warn(`Input with name="${name}" not found.`);
    }
}

async function fetchNetworkConfig() {
    const apiUrl = '/api/getEthernetInfo';

    try {
        const response = await fetch(apiUrl);
        console.log('API response:', response);

        if (!response.ok) {
            throw new Error(`Network response was not ok (status: ${response.status})`);
        }

        const jsonData = await response.json();
        const ethernetList = jsonData?.ethernets;

        if (!Array.isArray(ethernetList) || ethernetList.length === 0) {
            throw new Error('Ethernet data is missing or not in expected format.');
        }

        const config = ethernetList[0];

        // Populate form fields
        setInputValue('ip', config?.ip ?? '');
        setInputValue('mask', config?.mask ?? '');
        setInputValue('gateway', config?.gateWay ?? '');
        setInputValue('dns1', config?.dns?.[0] ?? '');
        setInputValue('dns2', config?.dns?.[1] ?? '');

        console.log('Network config loaded:', config);
    } catch (error) {
        console.error('Failed to fetch network config:', error);
    }
}

async function fetchControllerConfig() {
    fetch("/api/config") // Adjust to your actual GET endpoint
        .then(async response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const result = await response.json();
            console.log("Controller config fetched:", result);
            return result
            
})
        .then(data => {
            document.getElementById("controllerIp").value = data.controllerIp;
            document.getElementById("wbFile").value = data.wbFile;
            document.getElementById("baseUrl").value = data.baseUrl;
        })
        .catch(err => console.error("Error fetching config:", err));
}

async function fetchStatus() {
    try {
        const response = await fetch("/api/status");
        if (!response.ok) throw new Error("Failed to fetch status");
        const status = await response.json();

        document.getElementById("sdkStatus").textContent = status.sdkInitialized ? "✅ Yes" : "❌ No";
        document.getElementById("loginStatus").textContent = status.login ? "✅ Yes" : "❌ No";
        document.getElementById("controllerIpDisplay").textContent = status.controllerIp;
        document.getElementById("ipReachable").textContent = status.ipReachable ? "✅ Reachable" : "❌ Unreachable";
        document.getElementById("loginBtn").style.display = status.login ? "none" : "inline-block";
    } catch (err) {
        console.error("Error fetching system status:", err);
    }
}

async function manualLogin() {
    try {
        const response = await fetch("/api/login", {
            method: "POST"
        });

        const text = await response.text();
        alert(`Login response: ${text}`);

        // Re-fetch status after login attempt
        fetchStatus();
    } catch (err) {
        console.error("Login request failed:", err);
        alert("Login failed.");
    }
}


async function submitControllerConfig() {
    const config = {
        controllerIp: document.getElementById("controllerIp").value,
        wbFile: document.getElementById("wbFile").value,
        baseUrl: document.getElementById("baseUrl").value
    };

    fetch("/api/config", {
        method: "POST", // Or PUT
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(config)
    })
    .then(res => {
        if (res.ok) {
            alert("Configuration updated successfully!");
        } else {
            alert("Failed to update configuration.");
        }
    })
    .catch(err => console.error("Update failed:", err));
}

async function postNetworkConfig(event) {
    event.preventDefault();  // Prevent actual form submit

    // Grab form input values
    const ip = document.querySelector('input[name="ip"]').value.trim();
    const mask = document.querySelector('input[name="mask"]').value.trim();
    const gateway = document.querySelector('input[name="gateway"]').value.trim();
    const dns1 = document.querySelector('input[name="dns1"]').value.trim();
    const dns2 = document.querySelector('input[name="dns2"]').value.trim();

    // Build DNS array ignoring empty values
    const dnsServers = [dns1, dns2].filter(dns => dns !== '');

    // Create payload object matching EthernetConfig structure expected by backend
    const payload = {
        ip,
        mask,
        gateway,
        dnsServers
    };

    try {
        const response = await fetch('/api/setEthernetInfo', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const text = await response.text();
            throw new Error(`Failed to set config: ${response.status} - ${text}`);
        }

        const result = await response.text();  // or json() if your API returns JSON
        
        
        alert('Network configuration updated successfully!\n' + result);

    } catch (error) {
        alert('Error updating network configuration:\n' + error.message);
        console.error('POST error:', error);
    }
}

// Fetch network config once page is loaded
window.addEventListener('DOMContentLoaded', () => {
    console.log('DOM fully loaded, fetching network config...');
     fetchStatus();
    fetchNetworkConfig();
    fetchControllerConfig();

});
