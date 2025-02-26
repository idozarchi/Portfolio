export default function Button({ label, onClick }) {
    return (
      <button
        className="bg-gray-600 text-white px-6 py-2 rounded-lg hover:bg-gray-500"
        onClick={onClick}
      >
        {label}
      </button>
    );
  }
  