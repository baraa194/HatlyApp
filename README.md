# 🛵 HatlyApp - End-to-End Food Delivery & Real-Time Tracking Platform

**HatlyApp** is a production-grade, full-stack food delivery application designed with a robust micro-architecture to provide seamless workflows for Customers, Restaurants, and Delivery Agents. The system leverages high-performance caching and real-time geospatial mechanics to handle rapid order dispatching and live tracking.

---

## 🚀 Core Features & Business Logic

### 🔐 1. Secure Authentication Architecture
* **User Management:** Full authentication cycle supporting secure **Registration**, **Login**, and **Password Reset** workflows.
* **Security Guards:** Stateless security layer powered by **Spring Security** and **JWT (JSON Web Tokens)** to enforce strict role-based access control (RBAC).

* <p align="center">

<img src="https://github.com/user-attachments/assets/32ffabc1-50e0-413a-beda-793fd82e51a1" alt="Register" width="280"/>
   <img src="https://github.com/user-attachments/assets/1d9125f1-f1c8-40f4-8961-3c118fe64dcf"  alt="Login" width="280"/>
        <img src="https://github.com/user-attachments/assets/98f29dcf-0c66-416d-9c68-e3acd0882db3" alt="Reset Password" width="280"/>

</p>

### 🏪 2. Smart Location-Based Restaurant Browsing
* **Multi-Branch Hierarchy:** Restaurants can manage multiple physical branches.
* **Auto-Nearest Branch:** When a customer opens a restaurant, the system automatically computes and displays the **nearest branch to their home address**, streamlining the menu browsing experience.
* **Flexible Checkout:** Users can place items into their cart and complete checkout via **Stripe Credit Card Integration** or **Cash on Delivery (COD)**.
* <p align="center">


   <img src="https://github.com/user-attachments/assets/3aac0858-df26-4ad4-bccf-896f8bc53d11" alt="Restaurants" width="280"/>
    <img src="https://github.com/user-attachments/assets/d814ba92-bab0-4a16-a37e-204ff87243f8" alt="Nearst Branch" width="280"/>
     <img src="https://github.com/user-attachments/assets/ad0d0497-45f9-4c0b-a706-941f6a95525f" alt="Cart" width="280"/>
      <img src="https://github.com/user-attachments/assets/70e88100-6fa8-4d51-8170-989fc048fc34" alt="Credit Payment" width="280"/>


   
</p>


### ⚡ 3. Intelligent Real-Time Dispatch System
* **Criteria-Based Broadcast:** Once an order is placed, an instantaneous **real-time notification** is triggered to delivery agents who meet three strict conditions:
  1. Currently **Online**.
  2. Marked as **Available** (not currently on a trip).
  3. Geographically located **closest to the target restaurant branch**.
* **Live Order Tracking:** As soon as an agent accepts the order:
  * The agent can view the customer's precise drop-off location on the map along with shipping details.
  * The customer receives immediate status updates (Pending $\rightarrow$ Out for Delivery $\rightarrow$ Delivered) and can **track the delivery agent's live movement on an interactive map**.
* <p align="center">


   <img src="https://github.com/user-attachments/assets/5e0463c1-da8f-4355-bd23-972e60c3ed9c" alt="Delivery Notifications" width="280"/>
    <img src="https://github.com/user-attachments/assets/f5b5cb29-05f2-4954-898b-682a47d4e537" alt="Customer track Order" width="280"/>

   
</p>
---

## 🛠️ Technical Implementation (Backend)

Built using **Java & Spring Boot**, the backend emphasizes database optimization, strict caching boundaries, and high-frequency data handling.

* **Decoupled Media Storage (Object Storage Bucket):** Optimized for maximum server performance and zero disk-space overhead; restaurant logos and product images are never saved locally on the backend server. Instead, they are securely offloaded and served directly via cloud-ready **Object Storage Buckets**, keeping the API fast, stateless, and fully scalable.
* **High-Frequency Caching (Redis):** * Used to store and update delivery agent locations via a **10-second tracking interval background service**. Storing coordinates in memory eliminates complex, resource-heavy SQL database calculations.
  * Applied to heavy, read-intensive data like **Restaurants** and **Products** to handle massive concurrent traffic effortlessly.
* **Relational Persistence:** Backed by **PostgreSQL** for clean, transactional data mappings and relational integrity.
* **Testing Suite:** Covered by rigorous **Unit Testing (JUnit 5 & Mockito)** over critical business-layer core services, including:
  * `OrderService` (Financials, creation flow, and status transitions).
  * `RestaurantService` (Branch routing and inventory).
  * `DeliveryAgentService` (Notification assignment and status flags).

---

## 🎨 Frontend Architecture (Angular)

The client side is engineered with **Angular**, maximizing single-page performance and interactive UI responsiveness.

* **Reactive State Management:** Utilizes **RxJS Observables** and dynamic streams to consume live HTTP payloads and feed real-time map trackers.
* **Modular Codebase:** Implements clean feature-per-module encapsulation with strict **Angular Routing Guards** to lock down customer dashboards, restaurant panels, and agent views based on incoming JWT claims.
* **Interactive Maps Integration:** Seamless map components render live geometry lines, customer coordinates, and active delivery vehicle routes dynamically.





