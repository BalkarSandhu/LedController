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
    fetchNetworkConfig();

    const form = document.getElementById('networkConfigForm');
    form.addEventListener('submit', postNetworkConfig);
});
