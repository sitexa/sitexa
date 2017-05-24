import React from "react";
import axios from "axios";

class SunWeatherComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            sunrise: undefined,
            sunset: undefined,
            temperature: undefined
        };
    }

    componentDidMount = () => {
        axios.get('/weather', {
            params:{
                lat : 28.1791667,
                lng : 113.1136111
            }
        }).then((response) => {
            const json = response.data;
            this.setState({
                sunrise: json.sunInfo.sunrise,
                sunset: json.sunInfo.sunset,
                temperature: json.temperature
            });
        })
    };
    render = () => {
        return <table>
            <tbody>
            <tr>
                <td>Sunrise time:</td>
                <td>{this.state.sunrise}</td>
            </tr>
            <tr>
                <td>Sunset time:</td>
                <td>{this.state.sunset}</td>
            </tr>
            <tr>
                <td>Current temperature:</td>
                <td>{this.state.temperature}</td>
            </tr>
            </tbody>
        </table>
    }
}

export default SunWeatherComponent;