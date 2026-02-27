console.log("Contacts.js script");

// set the modal menu element
const viewContactModal = document.getElementById('view_contact_modal');

// options with default values
const options = {
    placement: 'bottom-right',
    backdrop: 'dynamic',
    backdropClasses:
        'bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40',
    closable: true,
    onHide: () => {
        console.log('modal is hidden');
    },
    onShow: () => {
        console.log('modal is shown');
    },
    onToggle: () => {
        console.log('modal has been toggled');
    },
};

// instance options object
const instanceOptions = {
    id: 'view_contact_modal',
    override: true
};

// Create a new Modal object based on the options above.
const contactModal = new Modal(viewContactModal, options, instanceOptions);

function  openContactModal() {
    contactModal.show();
}

function closeContactModal() {contactModal.hide();}

function loadContactData(id) {
    console.log("Loading contact data for ID:", id);
    fetch('/api/contacts/' + id) // makes HTTP GET request to our /api/contacts/{contactId} endpoint
        .then(response => response.json())  // converts HTTP response to JSON object, response.json() parses the JSON string from server
        .then(data => {  // data is the Contact object returned from your API
            console.log("Received data: ", data);
            document.getElementById('contact_name').textContent = data.name;
            document.getElementById('contact_email').textContent = data.email;
            document.getElementById('contact_phone').textContent = data.phoneNumber;
            document.getElementById('contact_address').textContent = data.address;
            if(data.favorite === true) {
                document.getElementById('contact_fav').innerHTML = '<span class="bg-green-100 text-green-800 text-xs font-medium mr-2 px-2.5 py-0.5 rounded dark:bg-green-900 dark:text-green-300">YES</span>';
            } else {
                document.getElementById('contact_fav').innerHTML = '<span class="bg-red-100 text-red-800 text-xs font-medium mr-2 px-2.5 py-0.5 rounded dark:bg-red-900 dark:text-red-300">NO</span>';
            }
            openContactModal();
        })
        .catch(error => console.error('Error in contacts.js:', error));
}
