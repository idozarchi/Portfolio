import "../src/app/globals.css"; // Import global CSS
import { useRouter } from "next/router"; // Import Next.js router
import { useState } from "react"; // Import useState for managing form visibility

import Button from "../components/Button";
import LoginForm from "../components/LoginForm";
import SignInForm from "../components/SignInForm";
import RegisterCompanyForm from "../components/RegisterCompanyForm";

export default function Home() {
  // State to toggle form visibility
  const [showLoginForm, setLoginForm] = useState(false);
  const [showSignInForm, setSignInForm] = useState(false);
  const [showRegisterCompanyForm, setRegisterCompanyForm] = useState(false);

  // State for form data
  const [loginData, setLoginData] = useState({ name: "", email: "" });
  const [signInData, setSignInData] = useState({
    name: "",
    email: "",
    company: "",
  });
  const [registerData, setRegisterData] = useState({
    company_name: "",
    company_address: "",
    company_id: "",
    contact_id: "",
    email: "",
    contact_name: "",
    phone_number: "",
    subscription_plan: "",
  });

  const router = useRouter(); // Initialize Next.js router

  // Handle input changes
  const handleChange = (e, form) => {
    const { name, value } = e.target;
    if (form === "login") {
      setLoginData((prevData) => ({ ...prevData, [name]: value }));
    } else if (form === "signIn") {
      setSignInData((prevData) => ({ ...prevData, [name]: value }));
    } else if (form === "registerCompany") {
      setRegisterData((prevData) => ({ ...prevData, [name]: value }));
    }
  };

  // Handle form visibility
  const handleLoginClick = () => setLoginForm(true);
  const handleSignInClick = () => setSignInForm(true);
  const handleRegisterCompanyClick = () => setRegisterCompanyForm(true);

  const closeLogin = () => setLoginForm(false);
  const closeSignIn = () => setSignInForm(false);
  const closeRegisterCompany = () => setRegisterCompanyForm(false);

  // Handle form submissions
  const handleSubmitForm = async (e, form) => {
    //e.preventDefault();
    let data;
    let url;

    if (form === "signIn") {
      data = signInData;
      url = "http://localhost:8080/IOTsWebsite/company"; // Change the URL
    } else if (form === "registerCompany") {
      data = e;
      url = "http://localhost:8080/IOTsWebsite/company"; // Change the URL
    }

    console.log(data);

    try {
      const response = await fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        alert("Form submitted successfully!");
        if (form === "registerCompany") {
          closeRegisterCompany();
        }
        setLoginData({});
        setSignInData({});
        setRegisterData({});
      } else {
        alert("Failed to submit form.");
      }
    } catch (error) {
      console.error("Error submitting form:", error);
      alert("Error submitting form.");
    }
  };

  const handleLoginSubmit = async (data, endpoint) => {
    /* In the condition I will validate the login*/

    if (true) {
      alert(`${endpoint} successful! Redirecting...`);
      closeLogin(false);
      closeSignIn(false);
      closeRegisterCompany(false);

      if (endpoint === "company") {
        router.push("/company"); // Redirect to company page
      }
    } else {
      alert(`Failed to ${endpoint}.`);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-r from-gray-300 to-gray-600 text-black">
      {/* Hero Section */}
      <section className="flex flex-col items-center justify-center text-center py-24 px-4">
        <h1 className="text-5xl font-extrabold leading-tight mb-4 drop-shadow-lg">
          Generic IoT Infrastructure
        </h1>
        <p className="text-xl mb-8 max-w-3xl mx-auto">
          Manage and monitor your IoT devices & data all in one place!
        </p>
      </section>

      {/* Main Section */}
      <section className="flex flex-col items-center justify-center text-center">
        <div className="flex flex-col gap-6">
          <Button label="Log In" onClick={handleLoginClick} />
          <Button label="Sign Up" onClick={handleSignInClick} />
          <Button
            label="Register Company"
            onClick={handleRegisterCompanyClick}
          />
        </div>
      </section>

      {showLoginForm && (
        <LoginForm
          onClose={() => closeLogin(false)}
          onSubmit={(data) => handleLoginSubmit(data, "company")}
        />
      )}

      {showSignInForm && (
        <SignInForm
          onClose={() => closeSignIn(false)}
          onSubmit={(data) => handleLoginSubmit(data, "")}
        />
      )}

      {showRegisterCompanyForm && (
        <RegisterCompanyForm
          onClose={() => closeRegisterCompany(false)}
          onSubmit={(data) => handleSubmitForm(data, "registerCompany")}
        />
      )}
    </div>
  );
}
