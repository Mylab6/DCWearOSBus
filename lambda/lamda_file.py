import os
import json
import http.client

def lambda_handler(event, context):
    # Check if the 'STOP_ID' parameter exists in the event


    api_key = os.environ.get('API_KEY')  # Retrieve API_KEY from environment variables
    if 'queryStringParameters' in event:
        event = event['queryStringParameters']
    if not api_key:
        return {
            "statusCode": 400,
            "body": json.dumps({"message": "API_KEY is missing in environment variables"})
        }

    try:
        conn = http.client.HTTPSConnection("api.wmata.com")
        headers = {
            "Host": "api.wmata.com",
            "api_key": api_key
        }
        request_url = None 
        if 'StopID' in event:
            stop_id = event['StopID']
            request_url = f"/NextBusService.svc/json/jPredictions?StopID={stop_id}"
            
        if 'Lat' in event:
            Lat = event['Lat'] 
            Lon = event['Lon']
            Radius = event['Radius'] #400
            request_url = f"/Bus.svc/json/jStops?Lat={Lat}&Lon={Lon}&Radius={Radius}"
        if request_url == None:
             return {
            "statusCode": 400,
            "body": json.dumps(event)
            }
        conn.request("GET", request_url, headers=headers)
        response = conn.getresponse()
        
        if response.status == 200:
            data = json.loads(response.read())
            # Handle the response data here
            print("Response data:", json.dumps(data, indent=2))
            return {
                "statusCode": 200,
                "body": json.dumps(data)
            }
        else:
            print("Request failed with status code:", response.status)
            return {
                "statusCode": response.status,
                "body": "Request failed"
            }
    except Exception as e:
        print("Error:", str(e))
        return {
            "statusCode": 500,
            "body": "Error making GET request"
        }
