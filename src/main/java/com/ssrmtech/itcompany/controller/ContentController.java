package com.ssrmtech.itcompany.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/content")
public class ContentController {

    @GetMapping("/services")
    public ResponseEntity<List<Map<String, Object>>> getServices() {
        List<Map<String, Object>> services = Arrays.asList(
            Map.of(
                "title", "IT Staffing",
                "icon", "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" width=\"48\" height=\"48\"><path fill=\"currentColor\" d=\"M20 6h-4V4c0-1.1-.9-2-2-2h-4c-1.1 0-2 .9-2 2v2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zM10 4h4v2h-4V4zm10 16H4V8h16v12z\"/><path fill=\"currentColor\" d=\"M13 10h-2v3H8v2h3v3h2v-3h3v-2h-3z\"/></svg>",
                "description", "Connect with top tech talent across all technology domains to power your digital innovation."
            ),
            Map.of(
                "title", "Software Development",
                "icon", "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" width=\"48\" height=\"48\"><path fill=\"currentColor\" d=\"M9.4 16.6L4.8 12l4.6-4.6L8 6l-6 6 6 6 1.4-1.4zm5.2 0l4.6-4.6-4.6-4.6L16 6l6 6-6 6-1.4-1.4z\"/></svg>",
                "description", "Custom software solutions designed to meet your specific business requirements and challenges."
            ),
            Map.of(
                "title", "Cloud Services",
                "icon", "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" width=\"48\" height=\"48\"><path fill=\"currentColor\" d=\"M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96z\"/></svg>",
                "description", "Comprehensive cloud solutions for scalable, secure, and resilient infrastructure."
            ),
            Map.of(
                "title", "IT Consulting",
                "icon", "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" width=\"48\" height=\"48\"><path fill=\"currentColor\" d=\"M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14z\"/><path fill=\"currentColor\" d=\"M7 10h2v7H7zm4-3h2v10h-2zm4 6h2v4h-2z\"/></svg>",
                "description", "Strategic technology guidance to optimize your IT infrastructure and drive business growth."
            )
        );
        
        return ResponseEntity.ok(services);
    }

    @GetMapping("/technologies")
    public ResponseEntity<List<Map<String, Object>>> getTechnologies() {
        List<Map<String, Object>> technologies = Arrays.asList(
            Map.of(
                "name", "Web Development",
                "icon", "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" width=\"48\" height=\"48\"><path fill=\"currentColor\" d=\"M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm-5 14H4v-4h11v4zm0-5H4V9h11v4zm5 5h-4V9h4v9z\"/></svg>",
                "tags", Arrays.asList("React", "Angular", "Node.js", "Vue", "Next.js")
            ),
            Map.of(
                "name", "Cloud Computing",
                "icon", "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" width=\"48\" height=\"48\"><path fill=\"currentColor\" d=\"M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96z\"/></svg>",
                "tags", Arrays.asList("AWS", "Azure", "GCP", "Kubernetes", "Docker")
            ),
            Map.of(
                "name", "Data Science",
                "icon", "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" width=\"48\" height=\"48\"><path fill=\"currentColor\" d=\"M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z\"/></svg>",
                "tags", Arrays.asList("Python", "TensorFlow", "PyTorch", "Big Data", "ML")
            )
        );
        
        return ResponseEntity.ok(technologies);
    }

    @GetMapping("/industries")
    public ResponseEntity<List<Map<String, Object>>> getIndustries() {
        List<Map<String, Object>> industries = Arrays.asList(
            Map.of(
                "name", "Information Technology",
                "icon", "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" width=\"48\" height=\"48\"><path fill=\"currentColor\" d=\"M20 18c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2H4c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2H0v2h24v-2h-4zM4 6h16v10H4V6z\"/></svg>"
            ),
            Map.of(
                "name", "Finance & Banking",
                "icon", "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" width=\"48\" height=\"48\"><path fill=\"currentColor\" d=\"M4 10v7h3v-7H4zm6 0v7h3v-7h-3zM2 22h19v-3H2v3zm14-12v7h3v-7h-3zm-4.5-9L2 6v2h19V6l-9.5-5z\"/></svg>"
            ),
            Map.of(
                "name", "Healthcare",
                "icon", "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" width=\"48\" height=\"48\"><path fill=\"currentColor\" d=\"M19 3H5c-1.1 0-1.99.9-1.99 2L3 19c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-1 11h-4v4h-4v-4H6v-4h4V6h4v4h4v4z\"/></svg>"
            ),
            Map.of(
                "name", "Manufacturing",
                "icon", "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" width=\"48\" height=\"48\"><path fill=\"currentColor\" d=\"M12 7V3H2v18h20V7H12zM6 19H4v-2h2v2zm0-4H4v-2h2v2zm0-4H4V9h2v2zm0-4H4V5h2v2zm4 12H8v-2h2v2zm0-4H8v-2h2v2zm0-4H8V9h2v2zm0-4H8V5h2v2zm10 12h-8v-2h2v-2h-2v-2h2v-2h-2V9h8v10zm-2-8h-2v2h2v-2zm0 4h-2v2h2v-2z\"/></svg>"
            )
        );
        
        return ResponseEntity.ok(industries);
    }

    @GetMapping("/testimonials")
    public ResponseEntity<List<Map<String, Object>>> getTestimonials() {
        List<Map<String, Object>> testimonials = Arrays.asList(
            Map.of(
                "quote", "SSRM Tech helped us find the perfect development team in record time. Their understanding of our technical needs was impressive.",
                "name", "Michael Johnson",
                "title", "CTO, TechInnovate",
                "image", "https://randomuser.me/api/portraits/men/32.jpg"
            ),
            Map.of(
                "quote", "The quality of candidates provided by SSRM Tech was exceptional. They truly understand the tech industry and its unique requirements.",
                "name", "Sarah Williams",
                "title", "HR Director, DataSystems Inc.",
                "image", "https://randomuser.me/api/portraits/women/44.jpg"
            ),
            Map.of(
                "quote", "Working with SSRM Tech has transformed our hiring process. Their consultative approach and industry expertise are unmatched.",
                "name", "David Chen",
                "title", "CEO, CloudNative Solutions",
                "image", "https://randomuser.me/api/portraits/men/67.jpg"
            )
        );
        
        return ResponseEntity.ok(testimonials);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<Map<String, Object>>> getStats() {
        List<Map<String, Object>> stats = Arrays.asList(
            Map.of(
                "value", "500+",
                "label", "Successful Projects",
                "offset", "0"
            ),
            Map.of(
                "value", "95%",
                "label", "Client Satisfaction",
                "offset", "14"
            ),
            Map.of(
                "value", "200+",
                "label", "Partner Companies",
                "offset", "113"
            ),
            Map.of(
                "value", "10+",
                "label", "Years Experience",
                "offset", "198"
            )
        );
        
        return ResponseEntity.ok(stats);
    }
}